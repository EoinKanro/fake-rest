package io.github.eoinkanro.fakerest.ui.conf;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import io.github.eoinkanro.fakerest.ui.MainFrame;

public class UiModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MainFrame.class).in(Scopes.SINGLETON);
    }
}
