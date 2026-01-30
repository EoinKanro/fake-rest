package io.github.eoinkanro.fakerest.core.handler.impl;

import io.github.eoinkanro.fakerest.core.conf.impl.StaticHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.model.HttpRequest;
import io.github.eoinkanro.fakerest.core.model.HttpResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StaticHttpHandler implements HttpHandler {

    @Getter
    private final StaticHttpHandlerConfig config;
    private final HttpResponse httpResponse;

    @Override
    public HttpResponse process(HttpRequest request) {
        return httpResponse;
    }

}
