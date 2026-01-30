package io.github.eoinkanro.fakerest.core;


import io.github.eoinkanro.fakerest.core.conf.impl.StaticHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerFactory;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.handler.RegisterException;
import io.github.eoinkanro.fakerest.core.handler.impl.HttpHandlerFactoryImpl;
import io.github.eoinkanro.fakerest.core.handler.impl.HttpHandlerRegistryImpl;
import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import io.github.eoinkanro.fakerest.core.server.impl.JavalinServer;

public class FakeRest {

    public static void main(String[] args) throws RegisterException {
        HttpHandlerRegistry registry = new HttpHandlerRegistryImpl();
        HttpHandlerFactory factory = new HttpHandlerFactoryImpl(registry);

        JavalinServer server = new JavalinServer(registry);
        server.init();

        StaticHttpHandlerConfig config = StaticHttpHandlerConfig.builder()
            .path("/test")
            .method(HttpMethod.GET)
            .responseBody("Test body")
            .responseCode(201)
            .build();

        HttpHandler handler = factory.create(config);
        registry.register(handler);
    }
}