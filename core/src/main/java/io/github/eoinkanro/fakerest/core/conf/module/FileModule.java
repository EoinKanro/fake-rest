package io.github.eoinkanro.fakerest.core.conf.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import io.github.eoinkanro.fakerest.core.conf.file.YamlConfigurator;
import io.github.eoinkanro.fakerest.core.conf.file.YamlStartupLoader;

public class FileModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(YamlConfigurator.class).in(Scopes.SINGLETON);
        bind(YamlStartupLoader.class).in(Scopes.SINGLETON);
    }
}
