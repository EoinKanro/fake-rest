package io.github.eoinkanro.fakerest.core;

import io.avaje.inject.BeanScope;
import io.github.eoinkanro.fakerest.core.conf.*;

import java.util.List;

public class FakeRestCore {

    public static void main(String[] args) {
        try (BeanScope scope = BeanScope.builder().build()) {
            List<Initializable> initializables = scope.list(Initializable.class);
            initializables.forEach(Initializable::init);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> initializables.forEach(Initializable::close)));
        }
    }

}