package io.github.eoinkanro.fakerest.core.conf;

public interface Initializable {

    void init();

    default void close() {
        //default do nothing
    }

}
