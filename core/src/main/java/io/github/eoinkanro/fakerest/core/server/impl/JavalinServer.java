package io.github.eoinkanro.fakerest.core.server.impl;

import io.avaje.inject.Component;
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

@Singleton
@Component
@RequiredArgsConstructor
public class JavalinServer implements HttpServer {

    private static final String BASE_PATH_VARIABLE = "entrance";
    private static final String BASE_PATH = "/<" + BASE_PATH_VARIABLE + ">";

    private final HttpHandlerRegistry registry;

    //todo close server
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
            //todo port
            .start(8080);
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

            //todo request variables
            HttpResponse response = handler.process(
                HttpRequest.builder()
                .body(context.body())
                .build()
            );

            //todo what if null? error?
            context.status(response.getCode())
                .result(response.getBody());
        } catch (Exception e) {
            context.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .result("Application error");
        }
    }

}
