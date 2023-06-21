package io.github.eoinkanro.fakerest.core.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.FakeRestApplication;
import io.github.eoinkanro.fakerest.core.controller.BaseController;
import io.github.eoinkanro.fakerest.core.controller.CreateController;
import io.github.eoinkanro.fakerest.core.controller.DeleteController;
import io.github.eoinkanro.fakerest.core.controller.FakeController;
import io.github.eoinkanro.fakerest.core.controller.ReadController;
import io.github.eoinkanro.fakerest.core.controller.UpdateController;
import io.github.eoinkanro.fakerest.core.model.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.ControllerData;
import io.github.eoinkanro.fakerest.core.model.ControllerFunctionMode;
import io.github.eoinkanro.fakerest.core.model.ControllerSaveInfoMode;
import io.github.eoinkanro.fakerest.core.model.UriConfigHolder;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

@SpringBootTest(classes = FakeRestApplication.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ControllerMappingConfiguratorTest extends MappingConfiguratorTest{

  private static final String GROOVY_SCRIPT = "return new GroovyAnswer();";

  @Autowired
  private ControllerData controllerData;

  @SpyBean
  private ControllerMappingConfigurator controllerMappingConfigurator;

  private static Stream<Arguments> provideAllUrisMethodsFunctionMods() {
    List<Arguments> arguments = new ArrayList<>();
    RequestMethod[] requestMethods = RequestMethod.values();
    for (RequestMethod requestMethod : requestMethods) {
      for (ControllerFunctionMode functionMode : ControllerFunctionMode.values()) {
        for (String uri : allUris) {
          arguments.add(Arguments.of(uri, requestMethod, functionMode));
        }
      }
    }
    return Stream.of(arguments.toArray(new Arguments[0]));
  }

  private static Stream<Arguments> provideAllMethodsFunctionMods() {
    List<Arguments> arguments = new ArrayList<>();
    RequestMethod[] requestMethods = RequestMethod.values();
    for (RequestMethod requestMethod : requestMethods) {
      for (ControllerFunctionMode functionMode : ControllerFunctionMode.values()) {
        arguments.add(Arguments.of(requestMethod, functionMode));
      }
    }
    return Stream.of(arguments.toArray(new Arguments[0]));
  }

  private static Stream<Arguments> provideAllMethodsCRUDFunctionMods() {
    List<Arguments> arguments = new ArrayList<>();
    RequestMethod[] requestMethods = RequestMethod.values();
    List<ControllerFunctionMode> functionModes = new ArrayList<>();
    functionModes.add(ControllerFunctionMode.CREATE);
    functionModes.add(ControllerFunctionMode.READ);
    functionModes.add(ControllerFunctionMode.UPDATE);
    functionModes.add(ControllerFunctionMode.DELETE);
    for (RequestMethod requestMethod : requestMethods) {
      for (ControllerFunctionMode functionMode : functionModes) {
        arguments.add(Arguments.of(requestMethod, functionMode));
      }
    }
    return Stream.of(arguments.toArray(new Arguments[0]));
  }

  private static Stream<Arguments> provideAllMethodsCUDFunctionMods() {
    List<Arguments> arguments = new ArrayList<>();
    RequestMethod[] requestMethods = RequestMethod.values();
    List<ControllerFunctionMode> functionModes = new ArrayList<>();
    functionModes.add(ControllerFunctionMode.CREATE);
    functionModes.add(ControllerFunctionMode.UPDATE);
    functionModes.add(ControllerFunctionMode.DELETE);
    for (RequestMethod requestMethod : requestMethods) {
      for (ControllerFunctionMode functionMode : functionModes) {
        arguments.add(Arguments.of(requestMethod, functionMode));
      }
    }
    return Stream.of(arguments.toArray(new Arguments[0]));
  }

  @Test
  void initController_NullUri_ConfigException() {
    ControllerConfig controllerConfig = new ControllerConfig();
    assertThrows(ConfigException.class, () -> controllerMappingConfigurator.registerController(controllerConfig));
  }

  @Test
  void initController_EmptyUri_ConfigException() {
    ControllerConfig controllerConfig = new ControllerConfig();
    controllerConfig.setUri("");
    assertThrows(ConfigException.class, () -> controllerMappingConfigurator.registerController(controllerConfig));
  }

  @ParameterizedTest
  @MethodSource("provideAllUris")
  void initController_NullMethod_ConfigException(String uri) {
    ControllerConfig controllerConfig = new ControllerConfig();
    controllerConfig.setUri(uri);
    assertThrows(ConfigException.class, () -> controllerMappingConfigurator.registerController(controllerConfig));
  }

  @ParameterizedTest
  @MethodSource("provideAllUrisMethods")
  void initController_NullFunctionMode_ConfigException(String uri, RequestMethod method) {
    ControllerConfig controllerConfig = new ControllerConfig();
    controllerConfig.setUri(uri);
    controllerConfig.setMethod(method);
    assertThrows(ConfigException.class, () -> controllerMappingConfigurator.registerController(controllerConfig));
  }

  @ParameterizedTest
  @MethodSource("provideAllUrisMethodsFunctionMods")
  void initController_UriAlreadyExists_ConfigException(String uri, RequestMethod method, ControllerFunctionMode functionMode) throws ConfigException {
    setUpYamlOk();
    ControllerConfig controllerConfig = new ControllerConfig();
    controllerConfig.setUri(uri);
    controllerConfig.setMethod(method);
    controllerConfig.setFunctionMode(functionMode);
    controllerConfig.setGroovyScript(GROOVY_SCRIPT);
    controllerMappingConfigurator.registerController(controllerConfig);
    assertThrows(ConfigException.class, () -> controllerMappingConfigurator.registerController(controllerConfig));
  }

  @ParameterizedTest
  @MethodSource("provideAllMethodsFunctionMods")
  void initCollectionReadController_StaticBaseUriAlreadyExist_ConfigException(RequestMethod method, ControllerFunctionMode functionMode) throws ConfigException {
    setUpYamlOk();
    ControllerConfig staticController = new ControllerConfig();
    staticController.setUri(TEST_STATIC_URI_SLASH_IN_END);
    staticController.setMethod(method);
    staticController.setFunctionMode(functionMode);
    staticController.setGroovyScript(GROOVY_SCRIPT);
    controllerMappingConfigurator.registerController(staticController);

    ControllerConfig collectionReadController = new ControllerConfig();
    collectionReadController.setUri(TEST_COLLECTION_URI);
    collectionReadController.setMethod(method);
    collectionReadController.setFunctionMode(ControllerFunctionMode.READ);
    assertThrows(ConfigException.class, () -> controllerMappingConfigurator.registerController(collectionReadController));
  }

  @ParameterizedTest
  @MethodSource("provideAllMethodsCRUDFunctionMods")
  void initStaticCRUDController_Ok(RequestMethod method, ControllerFunctionMode functionMode) throws ConfigException, NoSuchFieldException, IllegalAccessException {
    setUpYamlOk();
    ControllerConfig staticController = new ControllerConfig();
    staticController.setAnswer(STATIC_ANSWER);
    staticController.setMethod(method);
    staticController.setFunctionMode(functionMode);
    staticController.setUri(TEST_STATIC_URI);
    controllerMappingConfigurator.registerController(staticController);

    assertTrue(mappingConfiguratorData.getMethodsUrls().containsKey(method));
    assertEquals(1, mappingConfiguratorData.getMethodsUrls().get(method).size());
    assertTrue(mappingConfiguratorData.getMethodsUrls().get(method).contains(TEST_STATIC_URI));

    assertEquals(1, mappingConfiguratorData.getControllers().size());
    assertTrue(mappingConfiguratorData.getControllers().containsKey(staticController.getId()));

    UriConfigHolder<ControllerConfig> uriConfigHolder = mappingConfiguratorData.getControllers().get(staticController.getId());
    assertEquals(1, uriConfigHolder.getRequestMappingInfo().size());
    assertTrue(handlerMapping.getHandlerMethods().containsKey(uriConfigHolder.getRequestMappingInfo().keySet().iterator().next()));
    assertEquals(1, uriConfigHolder.getUsedUrls().size());
    assertTrue(uriConfigHolder.getUsedUrls().contains(TEST_STATIC_URI));

    BaseController baseController = uriConfigHolder.getRequestMappingInfo().get(uriConfigHolder.getRequestMappingInfo().keySet().iterator().next());
    switch (staticController.getFunctionMode()) {
      case CREATE -> assertEquals(CreateController.class, baseController.getClass());
      case DELETE -> assertEquals(DeleteController.class, baseController.getClass());
      case UPDATE -> assertEquals(UpdateController.class, baseController.getClass());
      case READ -> assertEquals(ReadController.class, baseController.getClass());
      default -> throw new IllegalArgumentException("Unexpected function mode");
    }

    Field field = FakeController.class.getDeclaredField("saveInfoMode");
    field.setAccessible(true);
    assertEquals(ControllerSaveInfoMode.STATIC, field.get(baseController));
    field.setAccessible(false);
  }

  @ParameterizedTest
  @MethodSource("provideAllMethods")
  void initCollectionReadController_JsonAnswer_Ok(RequestMethod method) throws ConfigException, NoSuchFieldException, IllegalAccessException {
    setUpYamlOk();
    ControllerConfig collectionController = new ControllerConfig();
    collectionController.setAnswer(COLLECTION_JSON_ANSWER);
    collectionController.setMethod(method);
    collectionController.setFunctionMode(ControllerFunctionMode.READ);
    collectionController.setUri(TEST_COLLECTION_URI);
    controllerMappingConfigurator.registerController(collectionController);

    assertInitCollectionReadController(method, collectionController);
    assertInitJsonAnswer(collectionController);
  }

  @ParameterizedTest
  @MethodSource("provideAllMethods")
  void initCollectionReadController_ArrayJsonAnswer_Ok(RequestMethod method) throws ConfigException, NoSuchFieldException, IllegalAccessException {
    setUpYamlOk();
    ControllerConfig collectionController = new ControllerConfig();
    collectionController.setAnswer(COLLECTION_ARRAY_JSON_ANSWER);
    collectionController.setMethod(method);
    collectionController.setFunctionMode(ControllerFunctionMode.READ);
    collectionController.setUri(TEST_COLLECTION_URI);
    controllerMappingConfigurator.registerController(collectionController);

    assertInitCollectionReadController(method, collectionController);
    assertInitArrayJsonAnswer(collectionController);
  }

  @ParameterizedTest
  @MethodSource("provideAllMethodsCUDFunctionMods")
  void initCollectionCUDController_JsonAnswer_Ok(RequestMethod method, ControllerFunctionMode mode) throws ConfigException, NoSuchFieldException, IllegalAccessException {
    setUpYamlOk();
    ControllerConfig collectionController = new ControllerConfig();
    collectionController.setAnswer(COLLECTION_JSON_ANSWER);
    collectionController.setMethod(method);
    collectionController.setFunctionMode(mode);
    collectionController.setUri(TEST_COLLECTION_URI);
    controllerMappingConfigurator.registerController(collectionController);

    assertInitCollectionCUDController(method, collectionController);
    assertInitJsonAnswer(collectionController);
  }

  @ParameterizedTest
  @MethodSource("provideAllMethodsCUDFunctionMods")
  void initCollectionCUDController_ArrayJsonAnswer_Ok(RequestMethod method, ControllerFunctionMode mode) throws ConfigException, NoSuchFieldException, IllegalAccessException {
    setUpYamlOk();
    ControllerConfig collectionController = new ControllerConfig();
    collectionController.setAnswer(COLLECTION_ARRAY_JSON_ANSWER);
    collectionController.setMethod(method);
    collectionController.setFunctionMode(mode);
    collectionController.setUri(TEST_COLLECTION_URI);
    controllerMappingConfigurator.registerController(collectionController);

    assertInitCollectionCUDController(method, collectionController);
    assertInitArrayJsonAnswer(collectionController);
  }

  @ParameterizedTest
  @MethodSource("provideAllUrisMethods")
  void initGroovyController_NullGroovyScript_ConfigException(String uri, RequestMethod method) {
    ControllerConfig groovyController = new ControllerConfig();
    groovyController.setUri(uri);
    groovyController.setMethod(method);
    groovyController.setFunctionMode(ControllerFunctionMode.GROOVY);
    assertThrows(ConfigException.class, () -> controllerMappingConfigurator.registerController(groovyController));
  }

  @ParameterizedTest
  @MethodSource("provideAllUrisMethods")
  void initGroovyController_EmptyGroovyScript_ConfigException(String uri, RequestMethod method) {
    ControllerConfig groovyController = new ControllerConfig();
    groovyController.setUri(uri);
    groovyController.setMethod(method);
    groovyController.setFunctionMode(ControllerFunctionMode.GROOVY);
    groovyController.setGroovyScript("");
    assertThrows(ConfigException.class, () -> controllerMappingConfigurator.registerController(groovyController));
  }

  @ParameterizedTest
  @MethodSource("provideAllUrisMethods")
  void initGroovyController_Ok(String uri, RequestMethod method) throws ConfigException {
    setUpYamlOk();
    ControllerConfig groovyController = new ControllerConfig();
    groovyController.setUri(uri);
    groovyController.setMethod(method);
    groovyController.setFunctionMode(ControllerFunctionMode.GROOVY);
    groovyController.setGroovyScript(GROOVY_SCRIPT);
    controllerMappingConfigurator.registerController(groovyController);

    assertTrue(mappingConfiguratorData.getMethodsUrls().containsKey(method));
    assertEquals(1, mappingConfiguratorData.getMethodsUrls().get(method).size());
    assertTrue(mappingConfiguratorData.getMethodsUrls().get(method).contains(uri));

    assertEquals(1, mappingConfiguratorData.getControllers().size());
    assertTrue(mappingConfiguratorData.getControllers().containsKey(groovyController.getId()));

    UriConfigHolder<ControllerConfig> uriConfigHolder = mappingConfiguratorData.getControllers().get(groovyController.getId());
    assertEquals(1, uriConfigHolder.getRequestMappingInfo().size());
    assertTrue(handlerMapping.getHandlerMethods().containsKey(uriConfigHolder.getRequestMappingInfo().keySet().iterator().next()));
    assertEquals(1, uriConfigHolder.getUsedUrls().size());
    assertTrue(uriConfigHolder.getUsedUrls().contains(uri));
  }

  @ParameterizedTest
  @MethodSource("provideAllUrisMethodsFunctionMods")
  void unregisterController_Ok(String uri, RequestMethod method, ControllerFunctionMode functionMode) throws ConfigException {
    setUpYamlOk();
    ControllerConfig controller = new ControllerConfig();
    controller.setUri(uri);
    controller.setMethod(method);
    controller.setFunctionMode(functionMode);
    controller.setGroovyScript(GROOVY_SCRIPT);
    controllerMappingConfigurator.registerController(controller);

    when(yamlConfigurator.isControllerExist(controller)).thenReturn(true);

    UriConfigHolder<ControllerConfig> uriConfigHolder = mappingConfiguratorData.getControllers().get(controller.getId());
    List<RequestMappingInfo> requestMappingInfos = new ArrayList<>(uriConfigHolder.getRequestMappingInfo().keySet());

    controllerMappingConfigurator.unregisterController(controller.getId());
    assertTrue(mappingConfiguratorData.getMethodsUrls().containsKey(method));
    assertEquals(0, mappingConfiguratorData.getMethodsUrls().get(method).size());
    assertEquals(0, mappingConfiguratorData.getControllers().size());
    assertEquals(0, controllerData.getAllData(controller.getUri()).size());
    for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
      assertFalse(handlerMapping.getHandlerMethods().containsKey(requestMappingInfo));
    }
    verify(yamlConfigurator).deleteController(controller);
  }

  @ParameterizedTest
  @MethodSource("provideAllMethodsCRUDFunctionMods")
  void unregisterCollectionController_DataNotDeleted(RequestMethod method, ControllerFunctionMode functionMode) throws ConfigException {
    setUpYamlOk();
    ControllerConfig controller = new ControllerConfig();
    controller.setUri(TEST_COLLECTION_URI);
    controller.setMethod(method);
    controller.setFunctionMode(functionMode);
    controller.setAnswer(COLLECTION_JSON_ANSWER);
    controllerMappingConfigurator.registerController(controller);

    when(yamlConfigurator.isControllerExist(controller)).thenReturn(true);

    RequestMethod secondMethod = null;
    for (RequestMethod requestMethod : RequestMethod.values()) {
      if (requestMethod != method) {
        secondMethod = requestMethod;
        break;
      }
    }
    ControllerConfig secondController = new ControllerConfig();
    secondController.setUri(TEST_COLLECTION_URI);
    secondController.setMethod(secondMethod);
    secondController.setFunctionMode(functionMode);
    controllerMappingConfigurator.registerController(secondController);

    UriConfigHolder<ControllerConfig> uriConfigHolder = mappingConfiguratorData.getControllers().get(controller.getId());
    List<RequestMappingInfo> requestMappingInfos = new ArrayList<>(uriConfigHolder.getRequestMappingInfo().keySet());

    controllerMappingConfigurator.unregisterController(controller.getId());
    assertTrue(mappingConfiguratorData.getMethodsUrls().containsKey(method));
    assertEquals(0, mappingConfiguratorData.getMethodsUrls().get(method).size());
    assertNotEquals(0, mappingConfiguratorData.getControllers().size());
    for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
      assertFalse(handlerMapping.getHandlerMethods().containsKey(requestMappingInfo));
    }
    assertNotEquals(0, controllerData.getAllData(controller.getUri()).size());
    verify(yamlConfigurator).deleteController(controller);
  }

  @Test
  void unregisterController_ControllerNotExist_ConfigException() {
    assertThrows(ConfigException.class, () -> controllerMappingConfigurator.unregisterController(ID));
  }

  @ParameterizedTest
  @MethodSource("provideAllUrisMethodsFunctionMods")
  void initController_CantSaveYaml_UnregisterController(String uri, RequestMethod method, ControllerFunctionMode functionMode) throws ConfigException {
    when(yamlConfigurator.isControllerExist(any())).thenReturn(false);
    when(yamlConfigurator.addController(any())).thenReturn(false);

    ControllerConfig controller = new ControllerConfig();
    controller.setUri(uri);
    controller.setMethod(method);
    controller.setFunctionMode(functionMode);
    controller.setGroovyScript(GROOVY_SCRIPT);
    controller.setAnswer(COLLECTION_JSON_ANSWER);
    controllerMappingConfigurator.registerController(controller);
    verify(controllerMappingConfigurator).unregisterController(controller.getId());
  }

  private void assertInitCollectionReadController(RequestMethod method, ControllerConfig collectionController) throws NoSuchFieldException, IllegalAccessException {
    assertTrue(mappingConfiguratorData.getMethodsUrls().containsKey(method));
    assertEquals(2, mappingConfiguratorData.getMethodsUrls().get(method).size());
    assertTrue(mappingConfiguratorData.getMethodsUrls().get(method).contains(TEST_STATIC_URI_SLASH_IN_END));
    assertTrue(mappingConfiguratorData.getMethodsUrls().get(method).contains(TEST_COLLECTION_URI));

    assertEquals(1, mappingConfiguratorData.getControllers().size());
    assertTrue(mappingConfiguratorData.getControllers().containsKey(collectionController.getId()));

    UriConfigHolder<ControllerConfig> uriConfigHolder = mappingConfiguratorData.getControllers().get(collectionController.getId());
    assertEquals(2, uriConfigHolder.getRequestMappingInfo().size());
    for (RequestMappingInfo requestMappingInfo : uriConfigHolder.getRequestMappingInfo().keySet()) {
      assertTrue(handlerMapping.getHandlerMethods().containsKey(requestMappingInfo));
    }
    assertEquals(2, uriConfigHolder.getUsedUrls().size());
    assertTrue(uriConfigHolder.getUsedUrls().contains(TEST_STATIC_URI_SLASH_IN_END));
    assertTrue(uriConfigHolder.getUsedUrls().contains(TEST_COLLECTION_URI));

    List<ControllerSaveInfoMode> activeMethods = new ArrayList<>();
    for (RequestMappingInfo requestMappingInfo : uriConfigHolder.getRequestMappingInfo().keySet()) {
      BaseController baseController = uriConfigHolder.getRequestMappingInfo().get(requestMappingInfo);
      assertEquals(ReadController.class, baseController.getClass());
      Field field = FakeController.class.getDeclaredField("saveInfoMode");
      field.setAccessible(true);
      activeMethods.add((ControllerSaveInfoMode) field.get(baseController));
      field.setAccessible(false);
    }

    assertEquals(2, activeMethods.size());
    assertTrue(activeMethods.contains(ControllerSaveInfoMode.COLLECTION_ONE));
    assertTrue(activeMethods.contains(ControllerSaveInfoMode.COLLECTION_ALL));
  }

  private void assertInitCollectionCUDController(RequestMethod method, ControllerConfig collectionController) throws NoSuchFieldException, IllegalAccessException {
    assertTrue(mappingConfiguratorData.getMethodsUrls().containsKey(method));
    assertEquals(1, mappingConfiguratorData.getMethodsUrls().get(method).size());

    if (collectionController.getFunctionMode() == ControllerFunctionMode.CREATE) {
      assertTrue(mappingConfiguratorData.getMethodsUrls().get(method).contains(TEST_STATIC_URI_SLASH_IN_END));
    } else {
      assertTrue(mappingConfiguratorData.getMethodsUrls().get(method).contains(TEST_COLLECTION_URI));
    }

    assertEquals(1, mappingConfiguratorData.getControllers().size());
    assertTrue(mappingConfiguratorData.getControllers().containsKey(collectionController.getId()));

    UriConfigHolder<ControllerConfig> uriConfigHolder = mappingConfiguratorData.getControllers().get(collectionController.getId());
    assertEquals(1, uriConfigHolder.getRequestMappingInfo().size());
    assertTrue(handlerMapping.getHandlerMethods().containsKey(uriConfigHolder.getRequestMappingInfo().keySet().iterator().next()));

    assertEquals(1, uriConfigHolder.getUsedUrls().size());
    if (collectionController.getFunctionMode() == ControllerFunctionMode.CREATE) {
      assertTrue(uriConfigHolder.getUsedUrls().contains(TEST_STATIC_URI_SLASH_IN_END));
    } else {
      assertTrue(uriConfigHolder.getUsedUrls().contains(TEST_COLLECTION_URI));
    }

    BaseController baseController = uriConfigHolder.getRequestMappingInfo().get(uriConfigHolder.getRequestMappingInfo().keySet().iterator().next());
    switch (collectionController.getFunctionMode()) {
      case CREATE -> assertEquals(CreateController.class, baseController.getClass());
      case DELETE -> assertEquals(DeleteController.class, baseController.getClass());
      case UPDATE -> assertEquals(UpdateController.class, baseController.getClass());
      default -> throw new IllegalArgumentException("Unexpected function mode");
    }

    Field field = FakeController.class.getDeclaredField("saveInfoMode");
    field.setAccessible(true);
    assertEquals(ControllerSaveInfoMode.COLLECTION_ONE, field.get(baseController));
    field.setAccessible(false);
  }

  private void assertInitJsonAnswer(ControllerConfig collectionController) {
    Map<String, ObjectNode> controllerAllData = controllerData.getAllData(collectionController.getUri());
    assertEquals(1, controllerAllData.size());
    assertEquals(JsonUtils.toJsonNode(COLLECTION_JSON_ANSWER), controllerAllData.get("1"));
  }

  private void assertInitArrayJsonAnswer(ControllerConfig collectionController) {
    Map<String, ObjectNode> controllerAllData = controllerData.getAllData(collectionController.getUri());
    assertEquals(2, controllerAllData.size());
    assertEquals(JsonUtils.toJsonNode(COLLECTION_ARRAY_JSON_ANSWER).get(0), controllerAllData.get("1"));
    assertEquals(JsonUtils.toJsonNode(COLLECTION_ARRAY_JSON_ANSWER).get(1), controllerAllData.get("2"));
  }

  private void setUpYamlOk() {
    when(yamlConfigurator.isControllerExist(any())).thenReturn(false);
    when(yamlConfigurator.addController(any())).thenReturn(true);
  }

}
