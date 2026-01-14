package io.github.eoinkanro.fakerest.core.server.impl;

import io.avaje.inject.Component;
import io.github.eoinkanro.fakerest.core.server.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Component
public class JavalinServer implements HttpServer {

    private static final String BASE_PATH_VARIABLE = "entrance";
    private static final String BASE_PATH = "/<" + BASE_PATH_VARIABLE + ">";

    private final Map<HttpMethod, Map<String, HttpHandler>> handlers = new ConcurrentHashMap<>();

    private Javalin server;

    @Override
    public void init() {
        server = Javalin.create()
            .get(BASE_PATH, ctx -> process(HttpMethod.GET, ctx))
            .post(BASE_PATH, ctx -> process(HttpMethod.POST, ctx))
            .put(BASE_PATH, ctx -> process(HttpMethod.PUT, ctx))
            .delete(BASE_PATH, ctx -> process(HttpMethod.DELETE, ctx))
            .head(BASE_PATH, ctx -> process(HttpMethod.HEAD, ctx))
            .options(BASE_PATH, ctx -> process(HttpMethod.OPTIONS, ctx))
            .patch(BASE_PATH, ctx -> process(HttpMethod.PATCH, ctx))
            .start(8080);
    }

    private void process(HttpMethod method, Context context) {
        try {
            String path = "/" + context.pathParam(BASE_PATH_VARIABLE);

            Map<String, HttpHandler> methodHandlers = handlers.computeIfAbsent(method, __ -> new ConcurrentHashMap<>());
            //todo groovy handler
            HttpHandler handler = methodHandlers.get(path);

            if (handler == null) {
                context.status(HttpStatus.NOT_FOUND)
                    .result("There is no handlers with path: " + path);
                return;
            }

            //todo request variables
            HttpResponse response = handler.process(HttpRequest.builder()
                .body(context.body())
                .build());

            context.status(response.getStatus())
                .result(response.getBody());
        } catch (Exception e) {
            context.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .result("Application error");
        }
    }

    @Override
    public void register(HttpMethod method, String path, HttpHandler handler) {
        Map<String, HttpHandler> methodHandlers = handlers.computeIfAbsent(method, __ -> new ConcurrentHashMap<>());

        if (methodHandlers.containsKey(path)) {
            //todo exception
            throw new RuntimeException();
        }

        methodHandlers.put(path, handler);
    }

}
