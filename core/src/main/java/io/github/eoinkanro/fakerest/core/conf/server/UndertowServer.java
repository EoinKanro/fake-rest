package io.github.eoinkanro.fakerest.core.conf.server;

import io.github.eoinkanro.fakerest.core.controller.BaseController;
import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.github.eoinkanro.fakerest.core.model.Hookable;
import io.github.eoinkanro.fakerest.core.model.ShutdownHook;
import io.github.eoinkanro.fakerest.core.model.conf.BaseUriConfig;
import io.github.eoinkanro.fakerest.core.utils.DefaultPropertiesUtils;
import io.github.eoinkanro.fakerest.core.utils.udertow.RoutingGettableHandler;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UndertowServer implements Hookable {

    private final RoutingGettableHandler routingHandler = new RoutingGettableHandler();
    private Undertow server = null;

    public void registerController(BaseUriConfig config, BaseController controller) {
        routingHandler.add(config.getMethod().name(),
                config.getUri(),
                httpServerExchange -> {
                    if (httpServerExchange.isInIoThread()) {
                        if (httpServerExchange.isBlocking()) {
                            processRequest(httpServerExchange, controller);
                        } else {
                            httpServerExchange.startBlocking();
                            httpServerExchange.dispatch(() -> processRequest(httpServerExchange, controller));
                        }
                    }
                });
    }

    private void processRequest(HttpServerExchange httpServerExchange, BaseController controller) {
        ControllerResponse response = controller.handle(httpServerExchange);
        if (response.getHeaders() != null) {
            response.getHeaders().forEach((header, list) -> {
                HttpString headerHttpString = new HttpString(header);
                list.forEach(value -> httpServerExchange.getResponseHeaders()
                        .add(headerHttpString, value));
            });
        }

        httpServerExchange.setStatusCode(response.getStatus());
        if (response.getBody() != null) {
            httpServerExchange.getResponseSender().send(response.getBody());
        } else {
            httpServerExchange.endExchange();
        }
    }

    public void unregisterController(BaseUriConfig config) {
        routingHandler.remove(new HttpString(config.getMethod().name()), config.getUri());
    }

    public boolean hasController(BaseUriConfig config) {
        return routingHandler.hasUri(config.getMethod().name(), config.getUri());
    }

    public void start() {
        if (server != null) {
            log.warn("Undertow server is already started");
            return;
        }

        this.server = Undertow.builder()
                .addHttpListener(DefaultPropertiesUtils.getPort(), "localhost")
                .setHandler(routingHandler)
                .build();

        server.start();
    }

    @Override
    public ShutdownHook getHook() {
        return new ShutdownHook(() -> {
            if (server != null) {
                server.stop();
            }
        });
    }
}
