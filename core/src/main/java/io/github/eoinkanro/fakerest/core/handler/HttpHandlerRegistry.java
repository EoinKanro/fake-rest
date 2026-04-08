package io.github.eoinkanro.fakerest.core.handler;

import io.github.eoinkanro.fakerest.core.model.HttpMethod;

public interface HttpHandlerRegistry {

    void register(HttpHandler handler) throws RegisterException;

    void unregister(HttpMethod method, String path);

    HttpHandler find(HttpMethod method, String path);

    HttpHandler find(String id);

}
