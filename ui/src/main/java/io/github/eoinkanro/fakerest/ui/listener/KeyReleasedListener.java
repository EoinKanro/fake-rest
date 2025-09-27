package io.github.eoinkanro.fakerest.ui.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public interface KeyReleasedListener extends KeyListener {

    @Override
    default void keyTyped(KeyEvent e) {
        //do nothing
    }

    @Override
    default void keyPressed(KeyEvent e) {
        //do nothing
    }

}
