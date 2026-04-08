package io.github.eoinkanro.fakerest.ui;

import io.avaje.inject.BeanScope;
import io.github.eoinkanro.fakerest.core.CoreModule;
import io.github.eoinkanro.fakerest.core.conf.Initializable;

import java.util.List;

public class FakeRestUi {

    public static void main(String[] args) {
        try (BeanScope scope = BeanScope.builder()
                .modules(new CoreModule(), new UiModule())
                .build()) {
            List<Initializable> initializables = scope.list(Initializable.class);
            initializables.forEach(Initializable::init);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> initializables.forEach(Initializable::close)));
        }
    }

}
