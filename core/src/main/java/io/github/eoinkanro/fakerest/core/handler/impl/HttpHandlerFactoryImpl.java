package io.github.eoinkanro.fakerest.core.handler.impl;

import io.avaje.inject.Component;
import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.StaticHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerFactory;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.model.HttpResponse;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@Component
@RequiredArgsConstructor
public class HttpHandlerFactoryImpl implements HttpHandlerFactory {

    private final HttpHandlerRegistry registry;

    @Override
    public HttpHandler create(AbstractHttpHandlerConfig config) {
        return switch (config.getType()) {
            case STATIC -> createStaticHttphandler((StaticHttpHandlerConfig) config);
        };
    }

    private StaticHttpHandler createStaticHttphandler(StaticHttpHandlerConfig config) {
        return new StaticHttpHandler(config,
            HttpResponse.builder()
                .code(config.getResponseCode())
                .body(config.getResponseBody())
                .build()
        );
    }

}
