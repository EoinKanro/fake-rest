package io.github.eoinkanro.fakerest.core.conf;

public interface ConfigLoader {

    void save(Config config) throws SaveConfigException;

    Config load() throws LoadConfigException;

}
