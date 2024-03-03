package io.github.eoinkanro.fakerest.core.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShutdownHook extends Thread {

    private final Runnable runnable;

    @Override
    public void run() {
        runnable.run();
    }
}
