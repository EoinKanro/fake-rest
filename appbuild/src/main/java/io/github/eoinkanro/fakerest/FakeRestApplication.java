package io.github.eoinkanro.fakerest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import io.github.eoinkanro.fakerest.core.conf.module.FileModule;
import io.github.eoinkanro.fakerest.core.conf.module.ServerModule;
import io.github.eoinkanro.fakerest.core.conf.module.ShutdownHookModule;
import io.github.eoinkanro.fakerest.core.conf.file.YamlStartupLoader;
import io.github.eoinkanro.fakerest.core.model.ShutdownHook;
import io.github.eoinkanro.fakerest.ui.MainFrame;
import io.github.eoinkanro.fakerest.ui.conf.UiModule;

import java.util.Set;

public class FakeRestApplication {

    public static void main(String[] args) {
        Injector context = Guice.createInjector(
                new ServerModule(),
                new FileModule(),
                new ShutdownHookModule(),
                new UiModule()
        );

        Set<ShutdownHook> shutdownHookSet = (Set<ShutdownHook>) context.getInstance(Key.get(TypeLiteral.get(Types.setOf(ShutdownHook.class))));
        shutdownHookSet.forEach(hook -> Runtime.getRuntime().addShutdownHook(hook));

        YamlStartupLoader yamlStartupLoader = context.getInstance(YamlStartupLoader.class);
        yamlStartupLoader.loadConfiguration();

        MainFrame mainFrame = context.getInstance(MainFrame.class);
        mainFrame.setVisible(true);
    }
}
