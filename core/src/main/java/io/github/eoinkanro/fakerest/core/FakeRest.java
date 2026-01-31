package io.github.eoinkanro.fakerest.core;


import io.github.eoinkanro.fakerest.core.conf.Config;
import io.github.eoinkanro.fakerest.core.conf.ConfigLoader;
import io.github.eoinkanro.fakerest.core.conf.LoadConfigException;
import io.github.eoinkanro.fakerest.core.conf.SaveConfigException;
import io.github.eoinkanro.fakerest.core.conf.impl.FileConfigLoader;
import io.github.eoinkanro.fakerest.core.conf.impl.GroovyHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.RouterHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.StaticHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.*;
import io.github.eoinkanro.fakerest.core.handler.impl.HttpHandlerDataRegistryImpl;
import io.github.eoinkanro.fakerest.core.handler.impl.HttpHandlerFactoryImpl;
import io.github.eoinkanro.fakerest.core.handler.impl.HttpHandlerRegistryImpl;
import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import io.github.eoinkanro.fakerest.core.server.impl.JavalinServer;

import java.util.Set;

public class FakeRest {

    public static void main(String[] args) throws RegisterException, SaveConfigException, LoadConfigException {
        HttpHandlerRegistry registry = new HttpHandlerRegistryImpl();
        HttpHandlerDataRegistry dataRegistry = new HttpHandlerDataRegistryImpl();
        HttpHandlerFactory factory = new HttpHandlerFactoryImpl(registry, dataRegistry);

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

        StaticHttpHandlerConfig staticNullConfig = StaticHttpHandlerConfig.builder()
            .path("/static/null")
            .method(HttpMethod.GET)
            .responseBody(null)
            .responseCode(606)
            .build();

        HttpHandler staticNullHandler = factory.create(staticNullConfig);
        registry.register(staticNullHandler);

        GroovyHttpHandlerConfig groovyConfig = GroovyHttpHandlerConfig.builder()
            .path("/groovy")
            .method(HttpMethod.GET)
            .groovyCode("return new HttpResponse(500, \"Hello from groovy\");")
            .build();

        HttpHandler groovyHandler = factory.create(groovyConfig);
        registry.register(groovyHandler);

        GroovyHttpHandlerConfig groovyVariablesConfig = GroovyHttpHandlerConfig.builder()
            .path("/groovy/variables")
            .method(HttpMethod.GET)
            .groovyCode("return new HttpResponse(500, request.getVariables().get(\"key\"));")
            .build();

        HttpHandler groovyVariablesHandler = factory.create(groovyVariablesConfig);
        registry.register(groovyVariablesHandler);

        GroovyHttpHandlerConfig groovyJsonConfig = GroovyHttpHandlerConfig.builder()
            .path("/groovy/json")
            .method(HttpMethod.GET)
            .groovyCode("""
                    ObjectNode json = jsonMapper.createObjectNode()
                    json.put("key",123)
                    
                    dataRegistry.put("123",json)
                    return new HttpResponse(200, dataRegistry.get("123").toString())
                    """)
            .build();

        HttpHandler groovyJsonHandler = factory.create(groovyJsonConfig);
        registry.register(groovyJsonHandler);

        RouterHttpHandlerConfig routerConfig = RouterHttpHandlerConfig.builder()
            .path("/route")
            .method(HttpMethod.GET)
            .routerPath("/static")
            .build();

        HttpHandler routerHandler = factory.create(routerConfig);
        registry.register(routerHandler);

        ConfigLoader configLoader = new FileConfigLoader();
        Config config = Config.builder()
            .port(1010)
            .handlers(Set.of(
                staticCOnfig,
                staticNullConfig,
                groovyConfig,
                groovyVariablesConfig,
                groovyJsonConfig
            ))
            .build();

        configLoader.save(config);
        config = configLoader.load();
        System.out.println(config);
    }
}