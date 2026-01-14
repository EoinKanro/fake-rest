package io.github.eoinkanro.fakerest.core.server.impl;

import io.github.eoinkanro.fakerest.core.server.HttpHandler;
import io.github.eoinkanro.fakerest.core.server.HttpRequest;
import io.github.eoinkanro.fakerest.core.server.HttpResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StaticHttpHandler implements HttpHandler {

    private final HttpResponse httpResponse;

    @Override
    public HttpResponse process(HttpRequest request) {
        return httpResponse;
    }

}
