package io.github.eoinkanro.fakerest.core.conf.impl;

import io.github.eoinkanro.fakerest.core.conf.Config;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerFactory;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.eoinkanro.fakerest.core.conf.impl.FileConfigLoader.*;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileConfigLoaderTest {

    private static final String IMPORT_JSON = "import.json";

    private static final String CONFIG1 = "{\"mockPort\":1010,\"uiPort\":2020,\"handlers\":[" +
        "{\"path\":\"/static\",\"method\":\"GET\",\"type\":\"STATIC\",\"responseBody\":\"body\",\"responseCode\":200}," +
        "{\"path\":\"/router\",\"method\":\"POST\",\"type\":\"ROUTER\",\"routerPath\":\"/toRoute\"}," +
        "{\"path\":\"/groovy\",\"method\":\"HEAD\",\"type\":\"GROOVY\",\"groovyCode\":\"myCode\"}" +
        "]}";

    private static final String CONFIG2 = "{\"mockPort\":123,\"uiPort\":456,\"handlers\":[" +
        "{\"path\":\"/static2\",\"method\":\"GET\",\"type\":\"STATIC\",\"responseBody\":\"body2\",\"responseCode\":202}]}";

    @TempDir
    private Path tempDir;

    @Mock
    private HttpHandlerRegistry handlerRegistry;
    @Mock
    private HttpHandlerFactory handlerFactory;

    private FileConfigLoader subject;

    private Path configPath;
    private Path importConfigPath;
    private Path processedImportConfigPath;

    @SneakyThrows
    private void init() {
        System.setProperty(APP_DIR, tempDir.toAbsolutePath().toString());
        configPath = tempDir.resolve(CONFIG_FILE);
        importConfigPath = tempDir.resolve(IMPORT_DIR).resolve(IMPORT_JSON);
        processedImportConfigPath = tempDir.resolve(IMPORT_DIR).resolve(IMPORT_PROCESSED_DIR).resolve(IMPORT_JSON);
        Files.createDirectories(processedImportConfigPath.getParent());
        subject = new FileConfigLoader(handlerRegistry, handlerFactory);
    }

    @SneakyThrows
    private void createTempConfig(Path path, String content) {
        Files.createDirectories(path.getParent());
        Files.write(path, content.getBytes(), CREATE, TRUNCATE_EXISTING);
    }

    @Test
    @SneakyThrows
    void testInit() {
        init();
        createTempConfig(configPath, CONFIG1);
        createTempConfig(importConfigPath, CONFIG2);

        subject.init();
        verify(handlerFactory, times(4)).create(any());
        verify(handlerRegistry, times(4)).register(any());
    }

    @Test
    @SneakyThrows
    void test_loadOrGetCached_thenReload() {
        init();
        createTempConfig(configPath, CONFIG1);
        createTempConfig(importConfigPath, CONFIG2);

        //test load
        Config config = subject.loadOrGetCached();
        assertConfigFrom_test_loadOrGetCached_thenReload(config);

        //test auto import moved
        assertFalse(Files.exists(importConfigPath));
        assertTrue(Files.exists(processedImportConfigPath));

        //test cache
        assertSame(config, subject.loadOrGetCached());

        //test reload
        Config config2 = subject.reload();
        assertNotSame(config, config2);

        assertConfigFrom_test_loadOrGetCached_thenReload(config2);
    }

    private void assertConfigFrom_test_loadOrGetCached_thenReload(Config config) {
        assertEquals(1010, config.getMockPort());
        assertEquals(2020, config.getUiPort());
        assertEquals(4, config.getHandlers().size());

        StaticHttpHandlerConfig staticConfig = (StaticHttpHandlerConfig) config.getHandlers().stream()
            .filter(conf -> conf.getPath().equals("/static"))
            .findAny().orElseThrow();

        assertEquals(HttpMethod.GET, staticConfig.getMethod());
        assertEquals("body", staticConfig.getResponseBody());
        assertEquals(200, staticConfig.getResponseCode());

        RouterHttpHandlerConfig routerConfig = (RouterHttpHandlerConfig) config.getHandlers().stream()
            .filter(conf -> conf.getPath().equals("/router"))
            .findAny().orElseThrow();

        assertEquals(HttpMethod.POST, routerConfig.getMethod());
        assertEquals("/toRoute", routerConfig.getRouterPath());

        GroovyHttpHandlerConfig groovyConfig = (GroovyHttpHandlerConfig) config.getHandlers().stream()
            .filter(conf -> conf.getPath().equals("/groovy"))
            .findAny().orElseThrow();

        assertEquals(HttpMethod.HEAD, groovyConfig.getMethod());
        assertEquals("myCode", groovyConfig.getGroovyCode());

        StaticHttpHandlerConfig staticConfig2 = (StaticHttpHandlerConfig) config.getHandlers().stream()
            .filter(conf -> conf.getPath().equals("/static2"))
            .findAny().orElseThrow();

        assertEquals(HttpMethod.GET, staticConfig2.getMethod());
        assertEquals("body2", staticConfig2.getResponseBody());
        assertEquals(202, staticConfig2.getResponseCode());
    }


    @Test
    @SneakyThrows
    void testSave() {
        init();

        Config config = Config.builder()
            .uiPort(1010)
            .build();

        subject.save(config);
        assertTrue(Files.exists(configPath));
    }

}
