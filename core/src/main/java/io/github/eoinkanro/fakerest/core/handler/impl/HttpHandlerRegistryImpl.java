package io.github.eoinkanro.fakerest.core.handler.impl;

import io.avaje.inject.Component;
import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.handler.RegisterException;
import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Component
public class HttpHandlerRegistryImpl implements HttpHandlerRegistry {

    private final Map<HttpMethod, Map<String, HttpHandler>> activeHandlers = new ConcurrentHashMap<>();

    @Override
    public void register(HttpHandler handler) throws RegisterException {
        AbstractHttpHandlerConfig config = handler.getConfig();
        Map<String, HttpHandler> handlers = activeHandlers.computeIfAbsent(config.getMethod(), __ -> new ConcurrentHashMap<>());

        if (handlers.containsKey(config.getPath())) {
            throw new RegisterException(String.format("Handler with method: %s and path: %s already exists",
                config.getMethod(),
                config.getPath()));
        }

        handlers.put(config.getPath(), handler);
    }

    @Override
    public void unregister(HttpMethod method, String path) {
        Map<String, HttpHandler> handlers = activeHandlers.computeIfAbsent(method, __ -> new ConcurrentHashMap<>());
        handlers.remove(path);
    }

    @Override
    public HttpHandler find(HttpMethod method, String path) {
        Map<String, HttpHandler> handlers = activeHandlers.computeIfAbsent(method, __ -> new ConcurrentHashMap<>());
        return handlers.get(path);
    }

}
