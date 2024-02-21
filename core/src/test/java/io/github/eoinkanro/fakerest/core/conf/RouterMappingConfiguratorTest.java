package io.github.eoinkanro.fakerest.core.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.eoinkanro.fakerest.core.FakeRestApplication;
import io.github.eoinkanro.fakerest.core.controller.BaseController;
import io.github.eoinkanro.fakerest.core.controller.RouterController;
import io.github.eoinkanro.fakerest.core.model.RouterConfig;
import io.github.eoinkanro.fakerest.core.model.UriConfigHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

@SpringBootTest(classes = FareRestTestApplication.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class RouterMappingConfiguratorTest extends MappingConfiguratorTest {

  @SpyBean
  private RouterMappingConfigurator routerMappingConfigurator;

  @Test
  void initRouter_NullUri_ConfigException() {
    RouterConfig routerConfig = new RouterConfig();
    assertThrows(ConfigException.class, () -> routerMappingConfigurator.registerRouter(routerConfig));
  }

  @Test
  void initRouter_EmptyUri_ConfigException() {
    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setUri("");
    assertThrows(ConfigException.class, () -> routerMappingConfigurator.registerRouter(routerConfig));
  }

  @ParameterizedTest
  @MethodSource("provideAllUris")
  void initRouter_nullToUrl_ConfigException(String uri) {
    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setUri(uri);
    assertThrows(ConfigException.class, () -> routerMappingConfigurator.registerRouter(routerConfig));
  }

  @ParameterizedTest
  @MethodSource("provideAllUris")
  void initRouter_EmptyToUrl_ConfigException(String uri) {
    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setUri(uri);
    routerConfig.setToUrl("");
    assertThrows(ConfigException.class, () -> routerMappingConfigurator.registerRouter(routerConfig));
  }

  @ParameterizedTest
  @MethodSource("provideAllUris")
  void initRouter_UriEqualsToUrl_ConfigException(String uri) {
    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setUri(uri);
    routerConfig.setToUrl(uri);
    assertThrows(ConfigException.class, () -> routerMappingConfigurator.registerRouter(routerConfig));
  }

  @ParameterizedTest
  @MethodSource("provideAllUris")
  void initRouter_NullMethod_ConfigException(String uri) {
    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setUri(uri);
    routerConfig.setToUrl(TEST_STATIC_URI_SLASH_IN_END);
    assertThrows(ConfigException.class, () -> routerMappingConfigurator.registerRouter(routerConfig));
  }

  @ParameterizedTest
  @MethodSource("provideAllUrisMethods")
  void initRouter_UriAlreadyExist_ConfigException(String uri, RequestMethod method) throws ConfigException {
    setUpYamlOk();
    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setUri(uri);
    routerConfig.setToUrl(TEST_STATIC_URI_SLASH_IN_END);
    routerConfig.setMethod(method);
    routerMappingConfigurator.registerRouter(routerConfig);

    assertThrows(ConfigException.class, () -> routerMappingConfigurator.registerRouter(routerConfig));
  }

  @ParameterizedTest
  @MethodSource("provideAllUrisMethods")
  void initRouter_Ok(String uri, RequestMethod method) throws ConfigException {
    setUpYamlOk();
    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setUri(uri);
    routerConfig.setToUrl(TEST_STATIC_URI_SLASH_IN_END);
    routerConfig.setMethod(method);
    routerMappingConfigurator.registerRouter(routerConfig);

    assertTrue(mappingConfiguratorData.getMethodsUrls().containsKey(method));
    assertEquals(1, mappingConfiguratorData.getMethodsUrls().get(method).size());

    assertTrue(mappingConfiguratorData.getMethodsUrls().get(method).contains(uri));

    assertEquals(1, mappingConfiguratorData.getRouters().size());
    assertTrue(mappingConfiguratorData.getRouters().containsKey(routerConfig.getId()));

    UriConfigHolder<RouterConfig> uriConfigHolder = mappingConfiguratorData.getRouters().get(routerConfig.getId());
    assertEquals(1, uriConfigHolder.getRequestMappingInfo().size());
    assertTrue(handlerMapping.getHandlerMethods().containsKey(uriConfigHolder.getRequestMappingInfo().keySet().iterator().next()));

    assertEquals(1, uriConfigHolder.getUsedUrls().size());
    assertTrue(uriConfigHolder.getUsedUrls().contains(uri));

    BaseController baseController = uriConfigHolder.getRequestMappingInfo().get(uriConfigHolder.getRequestMappingInfo().keySet().iterator().next());
    assertEquals(RouterController.class, baseController.getClass());
  }

  @ParameterizedTest
  @MethodSource("provideAllUrisMethods")
  void initRouter_CantSaveYaml_UnregisterRouter(String uri, RequestMethod method) throws ConfigException {
    when(yamlConfigurator.isRouterExist(any())).thenReturn(false);
    when(yamlConfigurator.addRouter(any())).thenReturn(false);

    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setUri(uri);
    routerConfig.setToUrl(TEST_STATIC_URI_SLASH_IN_END);
    routerConfig.setMethod(method);
    routerMappingConfigurator.registerRouter(routerConfig);
    verify(routerMappingConfigurator).unregisterRouter(routerConfig.getId());
  }

  @ParameterizedTest
  @MethodSource("provideAllUrisMethods")
  void unregisterRouter_Ok(String uri, RequestMethod method) throws ConfigException {
    setUpYamlOk();
    RouterConfig routerConfig = new RouterConfig();
    routerConfig.setUri(uri);
    routerConfig.setToUrl(TEST_STATIC_URI_SLASH_IN_END);
    routerConfig.setMethod(method);
    routerMappingConfigurator.registerRouter(routerConfig);

    when(yamlConfigurator.isRouterExist(routerConfig)).thenReturn(true);

    UriConfigHolder<RouterConfig> uriConfigHolder = mappingConfiguratorData.getRouters().get(routerConfig.getId());
    assertEquals(1, uriConfigHolder.getRequestMappingInfo().size());
    assertTrue(handlerMapping.getHandlerMethods().containsKey(uriConfigHolder.getRequestMappingInfo().keySet().iterator().next()));
    RequestMappingInfo routerConfigUriConfigHolder = uriConfigHolder.getRequestMappingInfo().keySet().iterator().next();

    routerMappingConfigurator.unregisterRouter(routerConfig.getId());

    assertTrue(mappingConfiguratorData.getMethodsUrls().containsKey(method));
    assertEquals(0, mappingConfiguratorData.getMethodsUrls().get(method).size());
    assertEquals(0, mappingConfiguratorData.getRouters().size());
    assertFalse(handlerMapping.getHandlerMethods().containsKey(routerConfigUriConfigHolder));
    verify(yamlConfigurator).deleteRouter(routerConfig);
  }

  @Test
  void unregisterCRouter_RouterNotExist_ConfigException() {
    assertThrows(ConfigException.class, () -> routerMappingConfigurator.unregisterRouter(ID));
  }

  private void setUpYamlOk() {
    when(yamlConfigurator.isRouterExist(any())).thenReturn(false);
    when(yamlConfigurator.addRouter(any())).thenReturn(true);
  }
}
