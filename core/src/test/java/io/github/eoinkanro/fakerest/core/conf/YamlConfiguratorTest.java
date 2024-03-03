package io.github.eoinkanro.fakerest.core.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.eoinkanro.fakerest.core.conf.file.YamlConfigurator;
import io.github.eoinkanro.fakerest.core.model.conf.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.enums.ControllerFunctionMode;
import io.github.eoinkanro.fakerest.core.model.conf.RouterConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import io.github.eoinkanro.fakerest.core.utils.DefaultPropertiesUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class YamlConfiguratorTest {

    private static final String TEST_FILE_NAME = "configuration-tests.yml";

    private static final String TEST_FILE_CONTENT = """
            ---
            rest:
              controllers:
              - uri: "/test/"
                method: "GET"
                functionMode: "READ"
                answer: null
                delayMs: 0
                idParams: []
                generateId: false
                generateIdPatterns: {}
                groovyScript: null
              routers:
              - uri: "/test"
                method: "GET"
                toUrl: "/test/"
            """;

    private static final String URI_WITH_SLASH = "/test/";
    private static final String URI_WITHOUT_SLASH = "/test";

    @InjectMocks
    private YamlConfigurator yamlConfigurator;

    @BeforeAll
    static void mockConfig() {
        var mock = Mockito.mockStatic(DefaultPropertiesUtils.class);
        mock.when(DefaultPropertiesUtils::getConfigFileName).thenReturn(TEST_FILE_NAME);
    }

    @AfterEach
    void clearConfig() throws Exception {
        File file = getConfig();
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file))) {
            fileWriter.write(TEST_FILE_CONTENT);
        }
    }

    @Test
    void isControllerExist_True() {
        ControllerConfig controllerConfig = new ControllerConfig();
        controllerConfig.setUri(URI_WITH_SLASH);
        controllerConfig.setMethod(HttpMethod.GET);
        controllerConfig.setFunctionMode(ControllerFunctionMode.READ);
        assertTrue(yamlConfigurator.isControllerExist(controllerConfig));
    }

    @Test
    void isControllerExist_False() {
        ControllerConfig controllerConfig = new ControllerConfig();
        controllerConfig.setUri(URI_WITHOUT_SLASH);
        controllerConfig.setMethod(HttpMethod.GET);
        controllerConfig.setFunctionMode(ControllerFunctionMode.READ);
        assertFalse(yamlConfigurator.isControllerExist(controllerConfig));
    }

    @Test
    void addController_Ok() {
        assertEquals(TEST_FILE_CONTENT, readConfigFile());
        ControllerConfig controllerConfig = new ControllerConfig();
        controllerConfig.setUri(URI_WITHOUT_SLASH);
        controllerConfig.setMethod(HttpMethod.GET);
        controllerConfig.setFunctionMode(ControllerFunctionMode.READ);
        assertTrue(yamlConfigurator.addController(controllerConfig));
        assertTrue(yamlConfigurator.isControllerExist(controllerConfig));
        assertNotEquals(TEST_FILE_CONTENT, readConfigFile());
    }

    @Test
    void deleteController_Ok() {
        assertEquals(TEST_FILE_CONTENT, readConfigFile());
        ControllerConfig controllerConfig = new ControllerConfig();
        controllerConfig.setUri(URI_WITH_SLASH);
        controllerConfig.setMethod(HttpMethod.GET);
        controllerConfig.setFunctionMode(ControllerFunctionMode.READ);
        yamlConfigurator.deleteController(controllerConfig);
        assertFalse(yamlConfigurator.isControllerExist(controllerConfig));
        assertNotEquals(TEST_FILE_CONTENT, readConfigFile());
    }

    @Test
    void isRouterExist_True() {
        RouterConfig routerConfig = new RouterConfig();
        routerConfig.setUri(URI_WITHOUT_SLASH);
        routerConfig.setToUrl(URI_WITH_SLASH);
        routerConfig.setMethod(HttpMethod.GET);
        assertTrue(yamlConfigurator.isRouterExist(routerConfig));
    }

    @Test
    void isRouterExist_False() {
        RouterConfig routerConfig = new RouterConfig();
        routerConfig.setUri(URI_WITH_SLASH);
        routerConfig.setToUrl(URI_WITHOUT_SLASH);
        routerConfig.setMethod(HttpMethod.GET);
        assertFalse(yamlConfigurator.isRouterExist(routerConfig));
    }

    @Test
    void addRouter_Ok() {
        assertEquals(TEST_FILE_CONTENT, readConfigFile());
        RouterConfig routerConfig = new RouterConfig();
        routerConfig.setUri(URI_WITH_SLASH);
        routerConfig.setToUrl(URI_WITHOUT_SLASH);
        routerConfig.setMethod(HttpMethod.GET);
        assertTrue(yamlConfigurator.addRouter(routerConfig));
        assertTrue(yamlConfigurator.isRouterExist(routerConfig));
        assertNotEquals(TEST_FILE_CONTENT, readConfigFile());
    }

    @Test
    void deleteRouter_Ok() {
        assertEquals(TEST_FILE_CONTENT, readConfigFile());
        RouterConfig routerConfig = new RouterConfig();
        routerConfig.setUri(URI_WITHOUT_SLASH);
        routerConfig.setToUrl(URI_WITH_SLASH);
        routerConfig.setMethod(HttpMethod.GET);
        yamlConfigurator.deleteRouter(routerConfig);
        assertFalse(yamlConfigurator.isRouterExist(routerConfig));
        assertNotEquals(TEST_FILE_CONTENT, readConfigFile());
    }

    @SneakyThrows
    private String readConfigFile() {
        StringBuilder stringBuilder = new StringBuilder();

        try (Stream<String> lines = Files.lines(getConfig().toPath(), StandardCharsets.UTF_8)) {
            lines.forEach(line -> {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            });
        }
        return stringBuilder.toString();
    }

    //TODO
    private File getConfig() {
        File config = new File(System.getProperty("user.dir"));
        return new File(config.getAbsolutePath() + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + TEST_FILE_NAME);
    }


}
