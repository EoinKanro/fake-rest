package io.github.eoinkanro.fakerest.core;


import io.github.eoinkanro.fakerest.core.server.HttpMethod;
import io.github.eoinkanro.fakerest.core.server.HttpResponse;
import io.github.eoinkanro.fakerest.core.server.impl.JavalinServer;
import io.github.eoinkanro.fakerest.core.server.impl.StaticHttpHandler;
import io.javalin.http.HttpStatus;

public class FakeRest {

    public static void main(String[] args) {
        JavalinServer server = new JavalinServer();
        server.init();

        server.register(HttpMethod.GET, "/test", new StaticHttpHandler(
            HttpResponse.builder()
                .status(HttpStatus.OK.getCode())
                .body("TESTTEST")
                .build()
        ));
    }
}