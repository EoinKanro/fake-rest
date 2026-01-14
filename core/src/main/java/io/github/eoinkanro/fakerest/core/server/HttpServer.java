package io.github.eoinkanro.fakerest.core.server;

import io.github.eoinkanro.fakerest.core.conf.Initializable;

public interface HttpServer extends Initializable {

    void register(HttpMethod method, String path, HttpHandler handler);

}
