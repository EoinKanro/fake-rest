package io.github.eoinkanro.fakerest.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.api.controller.MappingConfiguratorController;
import io.github.eoinkanro.fakerest.core.conf.ConfigException;
import io.github.eoinkanro.fakerest.core.conf.ControllerMappingConfigurator;
import io.github.eoinkanro.fakerest.core.conf.MappingConfigurationLoader;
import io.github.eoinkanro.fakerest.core.conf.MappingConfiguratorData;
import io.github.eoinkanro.fakerest.core.conf.RouterMappingConfigurator;
import io.github.eoinkanro.fakerest.core.model.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.ControllerFunctionMode;
import io.github.eoinkanro.fakerest.core.model.RouterConfig;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringBootTest(classes = FakeRestApiApplication.class)
@TestInstance(Lifecycle.PER_CLASS)
class MappingConfiguratorControllerTest {

  private static final String CONTROLLER_ID = "id";
  private static final String ROUTER_ID = "id2";

  @MockBean
  private MappingConfigurationLoader mappingConfigurationLoader;

  @MockBean
  private ControllerMappingConfigurator controllerMappingConfigurator;

  @MockBean
  private RouterMappingConfigurator routerMappingConfigurator;

  @MockBean
  private MappingConfiguratorData mappingConfiguratorData;

  @Autowired
  private MappingConfiguratorController mappingConfiguratorController;

  private List<ControllerConfig> controllerConfigs;
  private List<RouterConfig> routerConfigs;

  @BeforeAll
  void initConfigs() {
    controllerConfigs = new ArrayList<>();
    ControllerConfig controllerConfig = new ControllerConfig();
    controllerConfig.setId(CONTROLLER_ID);
    controllerConfig.setUri("/");
    controllerConfig.setFunctionMode(ControllerFunctionMode.READ);
    controllerConfig.setMethod(RequestMethod.GET);
    controllerConfigs.add(controllerConfig);

    routerConfigs = new ArrayList<>();
    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setId(ROUTER_ID);
    routerConfig.setUri("/test");
    routerConfig.setToUrl("/");
    routerConfig.setMethod(RequestMethod.GET);
    routerConfigs.add(routerConfig);
  }

  @Test
  void getAllControllersOk() {
    when(mappingConfiguratorData.getAllControllersCopy()).thenReturn(controllerConfigs);
    ResponseEntity<List<ControllerConfig>> response = mappingConfiguratorController.getAllControllers();
    assertEquals(controllerConfigs, response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getController_Ok() {
    when(mappingConfiguratorData.getControllerCopy(CONTROLLER_ID)).thenReturn(controllerConfigs.get(0));
    ResponseEntity<ControllerConfig> response = mappingConfiguratorController.getController(CONTROLLER_ID);
    assertEquals(controllerConfigs.get(0), response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getController_NotFound() {
    when(mappingConfiguratorData.getControllerCopy(CONTROLLER_ID)).thenReturn(null);
    ResponseEntity<ControllerConfig> response = mappingConfiguratorController.getController(CONTROLLER_ID);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void addController_NullConfig_BadRequest() {
    ResponseEntity<String> response = mappingConfiguratorController.addController(null);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void addController_BadConfig_BadRequest() throws ConfigException {
    doThrow(new ConfigException("")).when(controllerMappingConfigurator).registerController(any());
    ResponseEntity<String> response = mappingConfiguratorController.addController(new ControllerConfig());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void addController_UnexpectedError_InternalServerError() throws ConfigException {
    doThrow(new NullPointerException()).when(controllerMappingConfigurator).registerController(any());
    ResponseEntity<String> response = mappingConfiguratorController.addController(new ControllerConfig());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  void addController_Ok() throws ConfigException {
    ResponseEntity<String> response = mappingConfiguratorController.addController(controllerConfigs.get(0));
    verify(controllerMappingConfigurator).registerController(controllerConfigs.get(0));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(JsonUtils.toObjectNode(controllerConfigs.get(0)).toString(), response.getBody());
  }

  @Test
  void deleteController_NullConfig_BadRequest() {
    ResponseEntity<String> response = mappingConfiguratorController.deleteController(null);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void deleteController_BadConfig_BadRequest() throws ConfigException {
    doThrow(new ConfigException("")).when(controllerMappingConfigurator).unregisterController(any());
    ResponseEntity<String> response = mappingConfiguratorController.deleteController(CONTROLLER_ID);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void deleteController_UnexpectedError_InternalServerError() throws ConfigException {
    doThrow(new NullPointerException()).when(controllerMappingConfigurator).unregisterController(any());
    ResponseEntity<String> response = mappingConfiguratorController.deleteController(CONTROLLER_ID);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  void deleteController_Ok() throws ConfigException {
    when(mappingConfiguratorData.getControllerCopy(CONTROLLER_ID)).thenReturn(controllerConfigs.get(0));
    ResponseEntity<String> response = mappingConfiguratorController.deleteController(CONTROLLER_ID);
    verify(controllerMappingConfigurator).unregisterController(CONTROLLER_ID);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(JsonUtils.toObjectNode(controllerConfigs.get(0)).toString(), response.getBody());
  }

  @Test
  void getAllRoutersOk() {
    when(mappingConfiguratorData.getAllRoutersCopy()).thenReturn(routerConfigs);
    ResponseEntity<List<RouterConfig>> response = mappingConfiguratorController.getAllRouters();
    assertEquals(routerConfigs, response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getRouter_Ok() {
    when(mappingConfiguratorData.getRouterCopy(ROUTER_ID)).thenReturn(routerConfigs.get(0));
    ResponseEntity<RouterConfig> response = mappingConfiguratorController.getRouter(ROUTER_ID);
    assertEquals(routerConfigs.get(0), response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getRouter_NotFound() {
    when(mappingConfiguratorData.getControllerCopy(ROUTER_ID)).thenReturn(null);
    ResponseEntity<RouterConfig> response = mappingConfiguratorController.getRouter(ROUTER_ID);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void addCRouter_NullConfig_BadRequest() {
    ResponseEntity<String> response = mappingConfiguratorController.addRouter(null);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void addRouter_BadConfig_BadRequest() throws ConfigException {
    doThrow(new ConfigException("")).when(routerMappingConfigurator).registerRouter(any());
    ResponseEntity<String> response = mappingConfiguratorController.addRouter(new RouterConfig());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void addRouter_UnexpectedError_InternalServerError() throws ConfigException {
    doThrow(new NullPointerException()).when(routerMappingConfigurator).registerRouter(any());
    ResponseEntity<String> response = mappingConfiguratorController.addRouter(new RouterConfig());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  void addRouter_Ok() throws ConfigException {
    ResponseEntity<String> response = mappingConfiguratorController.addRouter(routerConfigs.get(0));
    verify(routerMappingConfigurator).registerRouter(routerConfigs.get(0));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(JsonUtils.toObjectNode(routerConfigs.get(0)).toString(), response.getBody());
  }

  @Test
  void deleteRouter_NullConfig_BadRequest() {
    ResponseEntity<String> response = mappingConfiguratorController.deleteRouter(null);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void deleteRouter_BadConfig_BadRequest() throws ConfigException {
    doThrow(new ConfigException("")).when(routerMappingConfigurator).unregisterRouter(any());
    ResponseEntity<String> response = mappingConfiguratorController.deleteRouter(ROUTER_ID);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void deleteRouter_UnexpectedError_InternalServerError() throws ConfigException {
    doThrow(new NullPointerException()).when(routerMappingConfigurator).unregisterRouter(any());
    ResponseEntity<String> response = mappingConfiguratorController.deleteController(ROUTER_ID);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  void deleteRouter_Ok() throws ConfigException {
    when(mappingConfiguratorData.getRouterCopy(ROUTER_ID)).thenReturn(routerConfigs.get(0));
    ResponseEntity<String> response = mappingConfiguratorController.deleteRouter(ROUTER_ID);
    verify(routerMappingConfigurator).unregisterRouter(ROUTER_ID);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(JsonUtils.toObjectNode(routerConfigs.get(0)).toString(), response.getBody());
  }
}
