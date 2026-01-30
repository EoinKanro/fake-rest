package io.github.eoinkanro.fakerest.core.handler.impl;

import io.github.eoinkanro.fakerest.core.conf.impl.RouterHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.model.HttpRequest;
import io.github.eoinkanro.fakerest.core.model.HttpResponse;
import io.javalin.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RouterHttpHandler implements HttpHandler {

    @Getter
    private final RouterHttpHandlerConfig config;
    private final HttpHandlerRegistry registry;

    @Override
    public HttpResponse process(HttpRequest request) {
        HttpHandler handler = registry.find(config.getMethod(), config.getRouterPath());

        if (handler == null) {
            return HttpResponse.builder()
                .code(HttpStatus.NOT_FOUND.getCode())
                .body("Cant' route to " + config.getRouterPath())
                .build();
        }

        return handler.process(request);
    }
}
