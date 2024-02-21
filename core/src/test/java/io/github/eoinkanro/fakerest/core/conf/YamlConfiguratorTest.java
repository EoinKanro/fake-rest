package io.github.eoinkanro.fakerest.core.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.eoinkanro.fakerest.core.FakeRestApplication;
import io.github.eoinkanro.fakerest.core.model.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.ControllerFunctionMode;
import io.github.eoinkanro.fakerest.core.model.RouterConfig;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringBootTest(classes = FareRestTestApplication.class)
@TestPropertySource(properties = {"spring.config.location = classpath:configuration-tests.yml"})
class YamlConfiguratorTest {

  private static final String TEST_FILE_NAME = "configuration-tests.yml";

  private static final String TEST_FILE_CONTENT = """
          ---
          rest:
            controllers:
              - uri: '/test/'
                method: GET
                functionMode: READ
            routers:
              - uri: '/test'
                toUrl: '/test/'
                method: GET
          """;

  private static final String URI_WITH_SLASH = "/test/";
  private static final String URI_WITHOUT_SLASH = "/test";

  @MockBean
  private MappingConfigurationLoader mappingConfigurationLoader;

  @Autowired
  private YamlConfigurator yamlConfigurator;

  @AfterEach
	void clearConfig() throws Exception{
		File file = new File(getClass().getClassLoader().getResource(TEST_FILE_NAME).getFile());
		try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file))) {
			fileWriter.write(TEST_FILE_CONTENT);
		}
	}

  @Test
  void isControllerExist_True() {
    ControllerConfig controllerConfig = new ControllerConfig();
    controllerConfig.setUri(URI_WITH_SLASH);
    controllerConfig.setMethod(RequestMethod.GET);
    controllerConfig.setFunctionMode(ControllerFunctionMode.READ);
    assertTrue(yamlConfigurator.isControllerExist(controllerConfig));
  }

  @Test
  void isControllerExist_False() {
    ControllerConfig controllerConfig = new ControllerConfig();
    controllerConfig.setUri(URI_WITHOUT_SLASH);
    controllerConfig.setMethod(RequestMethod.GET);
    controllerConfig.setFunctionMode(ControllerFunctionMode.READ);
    assertFalse(yamlConfigurator.isControllerExist(controllerConfig));
  }

  @Test
  void addController_Ok() {
    assertEquals(TEST_FILE_CONTENT, readConfigFile());
    ControllerConfig controllerConfig = new ControllerConfig();
    controllerConfig.setUri(URI_WITHOUT_SLASH);
    controllerConfig.setMethod(RequestMethod.GET);
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
    controllerConfig.setMethod(RequestMethod.GET);
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
    routerConfig.setMethod(RequestMethod.GET);
    assertTrue(yamlConfigurator.isRouterExist(routerConfig));
  }

  @Test
  void isRouterExist_False() {
    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setUri(URI_WITH_SLASH);
    routerConfig.setToUrl(URI_WITHOUT_SLASH);
    routerConfig.setMethod(RequestMethod.GET);
    assertFalse(yamlConfigurator.isRouterExist(routerConfig));
  }

  @Test
  void addRouter_Ok() {
    assertEquals(TEST_FILE_CONTENT, readConfigFile());
    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setUri(URI_WITH_SLASH);
    routerConfig.setToUrl(URI_WITHOUT_SLASH);
    routerConfig.setMethod(RequestMethod.GET);
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
    routerConfig.setMethod(RequestMethod.GET);
    yamlConfigurator.deleteRouter(routerConfig);
    assertFalse(yamlConfigurator.isRouterExist(routerConfig));
    assertNotEquals(TEST_FILE_CONTENT, readConfigFile());
  }

  @SneakyThrows
  private String readConfigFile() {
    StringBuilder stringBuilder = new StringBuilder();
    try (Stream<String> lines = Files.lines(Paths.get(getClass().getClassLoader().getResource(TEST_FILE_NAME).toURI()), StandardCharsets.UTF_8)) {
      lines.forEach(line -> {
        stringBuilder.append(line);
        stringBuilder.append("\n");
      });
    }
    return stringBuilder.toString();
  }


}
