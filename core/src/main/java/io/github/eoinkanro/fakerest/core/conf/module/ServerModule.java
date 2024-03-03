package io.github.eoinkanro.fakerest.core.conf.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import io.github.eoinkanro.fakerest.core.conf.server.MappingConfigurationsInfo;
import io.github.eoinkanro.fakerest.core.conf.server.UndertowServer;
import io.github.eoinkanro.fakerest.core.conf.server.controller.ControllerData;
import io.github.eoinkanro.fakerest.core.conf.server.controller.ControllerMappingConfigurator;
import io.github.eoinkanro.fakerest.core.conf.server.controller.RouterMappingConfigurator;
import io.github.eoinkanro.fakerest.core.utils.RestClient;
import jakarta.inject.Singleton;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MappingConfigurationsInfo.class).in(Scopes.SINGLETON);
        bind(ControllerData.class).in(Scopes.SINGLETON);
        bind(RouterMappingConfigurator.class).in(Scopes.SINGLETON);
        bind(ControllerMappingConfigurator.class).in(Scopes.SINGLETON);
        bind(ServerModule.class).in(Scopes.SINGLETON);
        bind(RestClient.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    public UndertowServer undertowServer() {
        UndertowServer undertowServer = new UndertowServer();
        undertowServer.start();
        return undertowServer;
    }

}
