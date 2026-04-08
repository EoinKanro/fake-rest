package io.github.eoinkanro.fakerest.ui.server;

import io.avaje.inject.External;
import io.github.eoinkanro.fakerest.core.conf.Config;
import io.github.eoinkanro.fakerest.core.conf.ConfigLoader;
import io.github.eoinkanro.fakerest.core.conf.Initializable;
import io.github.eoinkanro.fakerest.ui.handler.*;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class UiServer implements Initializable {

    public static final String ID_PATH = "id";

    private final CreateHttpHandlerApiHandler createHttpHandlerApiHandler;
    private final UpdateHttpHandlerApiHandler updateHttpHandlerApiHandler;
    private final DeleteHttpHandlerApiHandler deleteHttpHandlerApiHandler;

    private final GetConfigApiHandler getConfigApiHandler;
    private final UpdateConfigApiHandler updateConfigApiHandler;

    @External
    private final ConfigLoader configLoader;

    private Javalin server;

    @Override
    public void init() {
        int uiPort;
        try {
            uiPort = configLoader.loadOrGetCached().getUiPort();
        } catch (Exception e) {
            uiPort = Config.builder().build().getUiPort();
        }

        server = Javalin.create(cfg -> cfg.staticFiles.add("/public", Location.CLASSPATH))
            .start(uiPort);
    }

    @Override
    public void close() {
        server.stop();
    }
}
