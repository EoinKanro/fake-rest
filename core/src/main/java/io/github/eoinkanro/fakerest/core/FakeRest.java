package io.github.eoinkanro.fakerest.core;


import io.github.eoinkanro.fakerest.core.conf.impl.GroovyHttpHandlerConfig;
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

        StaticHttpHandlerConfig staticCOnfig = StaticHttpHandlerConfig.builder()
            .path("/static")
            .method(HttpMethod.GET)
            .responseBody("Hello from static")
            .responseCode(201)
            .build();

        HttpHandler staticHandler = factory.create(staticCOnfig);
        registry.register(staticHandler);

        GroovyHttpHandlerConfig groovyConfig = GroovyHttpHandlerConfig.builder()
            .path("/groovy")
            .method(HttpMethod.GET)
            .groovyCode("return new HttpResponse(500, \"Hello from groovy\");")
            .build();

        HttpHandler groovyHandler = factory.create(groovyConfig);
        registry.register(groovyHandler);
    }
}