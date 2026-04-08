package io.github.eoinkanro.fakerest.core.conf.impl;

import io.github.eoinkanro.fakerest.core.conf.*;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerFactory;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import jakarta.inject.Singleton;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Singleton
public class FileConfigLoader extends ConfigLoader {

    protected static final String APP_DIR = "user.dir";
    protected static final String CONFIG_FILE = "config.json";
    protected static final String IMPORT_DIR = "import";
    protected static final String IMPORT_PROCESSED_DIR = "processed";

    private final Path configFilePath;
    private final Path autoImportDirPath;
    private final Path autoImportProcessedDirPath;
    private final JsonMapper mapper;
    private final ReentrantLock lock;

    private Config cachedConfig;

    public FileConfigLoader(HttpHandlerRegistry registry, HttpHandlerFactory factory) throws IOException {
        super(registry, factory);

        String appPath = System.getProperty(APP_DIR);
        this.configFilePath = Path.of(appPath, CONFIG_FILE);
        this.autoImportDirPath = Path.of(appPath, IMPORT_DIR);
        this.autoImportProcessedDirPath = Path.of(appPath, IMPORT_DIR, IMPORT_PROCESSED_DIR);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(AbstractHttpHandlerConfig.class, new HttpHandlerConfigDeserializer());
        this.mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .addModule(module)
            .build();

        this.lock = new ReentrantLock();

        Files.createDirectories(autoImportProcessedDirPath);
    }

    @Override
    public void save(Config config) throws SaveConfigException {
        lock.lock();
        try {
            saveInternal(config);
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

        File configFile = configFilePath.toFile();
        Config config;
        if (configFile.exists() && configFile.isFile()) {
            config = mapper.readValue(configFilePath, Config.class);
        } else {
            config = Config.builder().build();
        }

        autoImport(config);
        cachedConfig = config;
        return cachedConfig;
    }

    /**
     * Auto import handlers from import folder into config
     * and save result into main config file
     *
     * @param config config to add additional handlers
     */
    private void autoImport(Config config) {
        File autoImportDir = autoImportDirPath.toFile();
        if (!autoImportDir.exists() || !autoImportDir.isDirectory()) {
            return;
        }

        for (File importFile : autoImportDir.listFiles()) {
            autoImport(config, importFile);
        }

        saveInternal(config);
    }

    /**
     * Read config from importFile, add handlers into config and move
     * importFile into processed folder
     *
     * @param config config to add additional handlers
     * @param importFile import
     */
    private void autoImport(Config config, File importFile) {
        try {
            if (!importFile.isFile()) {
                return;
            }

            Config importConfig = mapper.readValue(importFile, Config.class);
            if (importConfig.getHandlers() == null) {
                return;
            }
            if (config.getHandlers() == null) {
                config.setHandlers(importConfig.getHandlers());
                return;
            }

            importConfig.getHandlers().stream()
                .filter(handler -> !config.getHandlers().contains(handler))
                .forEach(handler -> {
                    handler.initId();
                    config.getHandlers().add(handler);
                });

            Path toMovePath = autoImportProcessedDirPath.resolve(importFile.getName());
            Files.move(importFile.toPath(), toMovePath, REPLACE_EXISTING);
        } catch (Exception e) {
            //todo log
        }
    }

    private void saveInternal(Config conf) {
        cachedConfig = null;
        mapper.writeValue(configFilePath, conf);
    }

}
