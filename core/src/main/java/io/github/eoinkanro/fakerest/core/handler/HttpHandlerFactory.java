package io.github.eoinkanro.fakerest.core.handler;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;

public interface HttpHandlerFactory {

    HttpHandler create(AbstractHttpHandlerConfig config);

}
