package io.github.eoinkanro.fakerest.core.handler.impl;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.handler.RegisterException;
import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class HttpHandlerRegistryImpl implements HttpHandlerRegistry {

    private final ReentrantLock registerLock = new ReentrantLock();

    private final Map<HttpMethod, Map<String, HttpHandler>> activeHandlersByMethodPath = new ConcurrentHashMap<>();
    private final Map<String, HttpHandler> activeHandlersById = new ConcurrentHashMap<>();

    @Override
    public void register(HttpHandler handler) throws RegisterException {
        try {
            registerLock.lock();
            AbstractHttpHandlerConfig config = handler.getConfig();

            String id = config.getId();
            if (id == null || id.isBlank()) {
                throw new RegisterException("Id of handler is empty");
            }

            Map<String, HttpHandler> handlers = activeHandlersByMethodPath.computeIfAbsent(config.getMethod(), __ -> new ConcurrentHashMap<>());
            if (handlers.containsKey(config.getPath())) {
                throw new RegisterException(String.format("Handler with method: %s and path: %s already exists",
                    config.getMethod(),
                    config.getPath()));
            }
            if (activeHandlersById.containsKey(id)) {
                throw new RegisterException(String.format("Handler with id %s already exist", id));
            }

            handlers.put(config.getPath(), handler);
            activeHandlersById.put(id, handler);
        } finally {
            registerLock.unlock();
        }
    }

    @Override
    public void unregister(HttpMethod method, String path) {
        try {
            registerLock.lock();
            Map<String, HttpHandler> handlers = activeHandlersByMethodPath.computeIfAbsent(method, __ -> new ConcurrentHashMap<>());
            HttpHandler removedHandler = handlers.remove(path);
            if (removedHandler != null) {
                activeHandlersById.remove(removedHandler.getConfig().getId());
            }
        } finally {
            registerLock.unlock();
        }

    }

    @Override
    public HttpHandler find(HttpMethod method, String path) {
        Map<String, HttpHandler> handlers = activeHandlersByMethodPath.computeIfAbsent(method, __ -> new ConcurrentHashMap<>());
        return handlers.get(path);
    }

    @Override
    public HttpHandler find(String id) {
        if (id == null) {
            return null;
        }
        return activeHandlersById.get(id);
    }

}
