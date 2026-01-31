package io.github.eoinkanro.fakerest.core.server.impl;

import io.avaje.inject.Component;
import io.github.eoinkanro.fakerest.core.conf.Config;
import io.github.eoinkanro.fakerest.core.conf.ConfigLoader;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.model.HttpRequest;
import io.github.eoinkanro.fakerest.core.model.HttpResponse;
import io.github.eoinkanro.fakerest.core.server.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

//todo logs
@Singleton
@Component
@RequiredArgsConstructor
public class JavalinServer implements HttpServer {

    private static final String BASE_PATH_VARIABLE = "entrance";
    private static final String BASE_PATH = "/<" + BASE_PATH_VARIABLE + ">";

    private final ConfigLoader configLoader;
    private final HttpHandlerRegistry registry;

    private Javalin server;

    @Override
    public void init() {
        int port = 8081;
        try {
            Config config = configLoader.load();
            if (config != null) {
                port = config.getPort();
            }
        } catch (Exception e) {
            //todo log
        }

        server = Javalin.create()
            .get(BASE_PATH, ctx -> process(HttpMethod.GET, ctx))
            .post(BASE_PATH, ctx -> process(HttpMethod.POST, ctx))
            .put(BASE_PATH, ctx -> process(HttpMethod.PUT, ctx))
            .delete(BASE_PATH, ctx -> process(HttpMethod.DELETE, ctx))
            .head(BASE_PATH, ctx -> process(HttpMethod.HEAD, ctx))
            .options(BASE_PATH, ctx -> process(HttpMethod.OPTIONS, ctx))
            .patch(BASE_PATH, ctx -> process(HttpMethod.PATCH, ctx))
            .start(port);
    }

    private void process(HttpMethod method, Context context) {
        try {
            String path = "/" + context.pathParam(BASE_PATH_VARIABLE);

            HttpHandler handler = registry.find(method, path);
            if (handler == null) {
                context.status(HttpStatus.NOT_FOUND)
                    .result("There is no handlers with path: " + path);
                return;
            }

            Map<String, String> variables = new HashMap<>();
            context.queryParamMap().forEach((key, values) -> {
                String value = String.join(", ", values);
                variables.put(key, value);
            });

            HttpRequest request = HttpRequest.builder()
                .body(context.body())
                .build();
            request.getVariables().putAll(variables);

            HttpResponse response = handler.process(request);

            context.status(response.getCode());
            if (response.getBody() == null) {
                context.result();
            } else {
                context.result(response.getBody());
            }
        } catch (Exception e) {
            context.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .result("Application error");
        }
    }

    @Override
    public void close() {
        server.stop();
    }

}
