package io.github.eoinkanro.fakerest.core.server;

public interface HttpHandler {

    HttpResponse process(HttpRequest request);

}
