package io.github.eoinkanro.fakerest.core.conf.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.eoinkanro.fakerest.core.conf.server.UndertowServer;
import io.github.eoinkanro.fakerest.core.model.ShutdownHook;
import jakarta.inject.Singleton;

import java.util.Set;

public class ShutdownHookModule extends AbstractModule {

    @Provides
    @Singleton
    public Set<ShutdownHook> shutdownHooks(UndertowServer server) {
        return Set.of(server.getHook());
    }

}
