package io.github.eoinkanro.fakerest.core.conf.impl;

import io.avaje.inject.Component;
import io.github.eoinkanro.fakerest.core.conf.*;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerFactory;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import jakarta.inject.Singleton;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.nio.file.Path;

@Singleton
@Component
public class FileConfigLoader extends ConfigLoader {

    private final Path configPath;
    private final JsonMapper mapper;

    public FileConfigLoader(HttpHandlerRegistry registry, HttpHandlerFactory factory) {
        super(registry, factory);

        this.configPath = Path.of(
            System.getProperty("user.dir"),
            "config.json");

        SimpleModule module = new SimpleModule();
        module.addDeserializer(AbstractHttpHandlerConfig.class, new HttpHandlerDeserializer());
        this.mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .addModule(module)
            .build();
    }

    @Override
    public void save(Config config) throws SaveConfigException {
        try {
            mapper.writeValue(configPath, config);
        } catch (Exception e) {
            throw new SaveConfigException(e);
        }
    }

    @Override
    public Config load() throws LoadConfigException {
        try {
            return mapper.readValue(configPath, Config.class);
        } catch (Exception e) {
            throw new LoadConfigException();
        }
    }

}
