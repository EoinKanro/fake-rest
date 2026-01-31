package io.github.eoinkanro.fakerest.core.conf.impl;

import io.github.eoinkanro.fakerest.core.conf.*;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerFactory;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import jakarta.inject.Singleton;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class FileConfigLoader extends ConfigLoader {

    private final Path configPath;
    private final JsonMapper mapper;
    private final ReentrantLock lock;

    private Config cachedConfig;

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

        this.lock = new ReentrantLock();
    }

    @Override
    public void save(Config conf) throws SaveConfigException {
        lock.lock();
        try {
            cachedConfig = null;
            mapper.writeValue(configPath, conf);
        } catch (Exception e) {
            throw new SaveConfigException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Config load() throws LoadConfigException {
        lock.lock();
        try {
            if (cachedConfig != null) {
                return cachedConfig;
            }

            cachedConfig = mapper.readValue(configPath, Config.class);
            return cachedConfig;
        } catch (Exception e) {
            throw new LoadConfigException();
        } finally {
            lock.unlock();
        }
    }

}
