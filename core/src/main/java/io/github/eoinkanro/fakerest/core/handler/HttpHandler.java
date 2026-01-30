package io.github.eoinkanro.fakerest.core.handler;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.model.HttpRequest;
import io.github.eoinkanro.fakerest.core.model.HttpResponse;

public interface HttpHandler {

    AbstractHttpHandlerConfig getConfig();

    HttpResponse process(HttpRequest request);

}
