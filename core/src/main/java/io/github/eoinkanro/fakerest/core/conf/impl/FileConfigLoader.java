package io.github.eoinkanro.fakerest.core.conf.impl;

import io.github.eoinkanro.fakerest.core.conf.*;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerFactory;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import jakarta.inject.Singleton;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class FileConfigLoader extends ConfigLoader {

    private static final String APP_DIR = "user.dir";
    private static final String CONFIG_FILE = "config.json";
    private static final String IMPORT_DIR = "import";

    private final Path configPath;
    private final Path autoImportPath;
    private final JsonMapper mapper;
    private final ReentrantLock lock;

    private Config cachedConfig;

    public FileConfigLoader(HttpHandlerRegistry registry, HttpHandlerFactory factory) {
        super(registry, factory);

        String appPath = System.getProperty(APP_DIR);
        this.configPath = Path.of(appPath, CONFIG_FILE);
        this.autoImportPath = Path.of(appPath, IMPORT_DIR);

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
    public Config loadOrGetCached() throws LoadConfigException {
        lock.lock();
        try {
            return loadOrGetCachedInternal();
        } catch (Exception e) {
            throw new LoadConfigException();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Config reload() throws LoadConfigException {
        lock.lock();
        try {
            cachedConfig = null;
            return loadOrGetCachedInternal();
        } catch (Exception e) {
            throw new LoadConfigException();
        } finally {
            lock.unlock();
        }
    }

    private Config loadOrGetCachedInternal() {
        if (cachedConfig != null) {
            return cachedConfig;
        }

        cachedConfig = mapper.readValue(configPath, Config.class);
        autoImport(cachedConfig);
        return cachedConfig;
    }

    private void autoImport(Config config) {
        File autoImportDir = autoImportPath.toFile();
        if (!autoImportDir.exists() || !autoImportDir.isDirectory()) {
            return;
        }

        for (File importFile : autoImportDir.listFiles()) {
            try {
                if (!importFile.isFile()) {
                    continue;
                }

                Config importConfig = mapper.readValue(importFile, Config.class);
                if (importConfig.getHandlers() == null) {
                    continue;
                }
                if (config.getHandlers() == null) {
                    config.setHandlers(importConfig.getHandlers());
                    continue;
                }

                importConfig.getHandlers().stream()
                    .filter(handler -> !config.getHandlers().contains(handler))
                    .forEach(handler -> config.getHandlers().add(handler));
            } catch (Exception e) {
                //todo log
            }
        }
    }

}
