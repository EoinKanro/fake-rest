package io.github.eoinkanro.fakerest.core.service;

import io.github.eoinkanro.fakerest.core.conf.*;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerFactory;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.handler.RegisterException;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor
public class HandlerConfigService {

    private final ReentrantLock lock = new ReentrantLock();

    private final HttpHandlerRegistry handlerRegistry;
    private final HttpHandlerFactory handlerFactory;
    private final ConfigLoader configLoader;

    public String addHandler(AbstractHttpHandlerConfig handlerConfig) throws RegisterException, LoadConfigException, SaveConfigException {
        try {
            lock.lock();
            handlerConfig.initId();
            HttpHandler handler = handlerFactory.create(handlerConfig);
            handlerRegistry.register(handler);

            Config config = configLoader.loadOrGetCached();
            config.getHandlers().add(handlerConfig);
            configLoader.save(config);
            return handlerConfig.getId();
        } finally {
            lock.unlock();
        }
    }

    public boolean deleteHandler(String id) throws LoadConfigException, SaveConfigException {
        try {
            lock.lock();
            HttpHandler handler = handlerRegistry.find(id);
            if (handler == null) {
                return false;
            }

            AbstractHttpHandlerConfig handlerConfig = handler.getConfig();
            handlerRegistry.unregister(handlerConfig.getMethod(), handlerConfig.getPath());

            Config config = configLoader.loadOrGetCached();
            config.setHandlers(config.getHandlers().stream()
                .filter(h -> !h.getId().equals(handlerConfig.getId()))
                .collect(Collectors.toSet()));
            configLoader.save(config);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean updateHandler(AbstractHttpHandlerConfig newHandlerConfig) throws RegisterException, LoadConfigException, SaveConfigException {
        try {
            lock.lock();
            HttpHandler oldHandler = handlerRegistry.find(newHandlerConfig.getId());
            if (oldHandler == null) {
                return false;
            }

            HttpHandler newHandler = handlerFactory.create(newHandlerConfig);
            AbstractHttpHandlerConfig oldConfigHandler = oldHandler.getConfig();
            handlerRegistry.unregister(oldConfigHandler.getMethod(), oldConfigHandler.getPath());

            if (handlerRegistry.find(newHandlerConfig.getMethod(), newHandlerConfig.getPath()) != null) {
                handlerRegistry.register(oldHandler);
                return false;
            }

            handlerRegistry.register(newHandler);
            Config config = configLoader.loadOrGetCached();
            config.setHandlers(config.getHandlers().stream()
                .filter(h -> !h.getId().equals(oldConfigHandler.getId()))
                .collect(Collectors.toSet()));
            config.getHandlers().add(newHandlerConfig);
            configLoader.save(config);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean updateMainConfig(Config config) throws LoadConfigException, SaveConfigException {
        try {
            lock.lock();

            Config newConfig = configLoader.loadOrGetCached()
                .toBuilder()
                .uiPort(config.getUiPort())
                .mockPort(config.getMockPort())
                .build();
            configLoader.save(newConfig);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public Config getConfig() throws LoadConfigException {
        try {
            lock.lock();
            return configLoader.loadOrGetCached();
        } finally {
            lock.unlock();
        }
    }

}
