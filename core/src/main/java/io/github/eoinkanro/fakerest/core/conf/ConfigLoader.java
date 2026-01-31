package io.github.eoinkanro.fakerest.core.conf;

import io.github.eoinkanro.fakerest.core.handler.HttpHandlerFactory;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.handler.RegisterException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ConfigLoader implements Initializable {

    private final HttpHandlerRegistry registry;
    private final HttpHandlerFactory factory;

    @Override
    public void init() {
        try {
            Config config = load();
            if (config == null || config.getHandlers() == null) {
                return;
            }

            config.getHandlers().forEach(conf -> {
                try {
                    registry.register(factory.create(conf));
                } catch (RegisterException e) {
                    //todo log
                }
            });
        } catch (Exception e) {
            //todo log
        }
    }

    public abstract void save(Config config) throws SaveConfigException;

    public abstract Config load() throws LoadConfigException;

}
