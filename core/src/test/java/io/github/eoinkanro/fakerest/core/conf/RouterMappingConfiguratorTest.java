package io.github.eoinkanro.fakerest.core.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.eoinkanro.fakerest.core.conf.server.controller.RouterMappingConfigurator;
import io.github.eoinkanro.fakerest.core.controller.BaseController;
import io.github.eoinkanro.fakerest.core.controller.RouterController;
import io.github.eoinkanro.fakerest.core.model.conf.BaseUriConfig;
import io.github.eoinkanro.fakerest.core.model.conf.RouterConfig;
import io.github.eoinkanro.fakerest.core.model.conf.UriConfigHolder;
import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RouterMappingConfiguratorTest extends AbstractMappingConfiguratorTest {

    @InjectMocks
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
    void initRouter_UriAlreadyExist_ConfigException(String uri, HttpMethod method) throws ConfigException {
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
    void initRouter_Ok(String uri, HttpMethod method) throws ConfigException {
        setUpYamlOk();
        RouterConfig routerConfig = new RouterConfig();
        routerConfig.setUri(uri);
        routerConfig.setToUrl(TEST_STATIC_URI_SLASH_IN_END);
        routerConfig.setMethod(method);
        routerMappingConfigurator.registerRouter(routerConfig);

        assertTrue(mappingConfigurationsInfo.getMethodsUrls().containsKey(method));
        assertEquals(1, mappingConfigurationsInfo.getMethodsUrls().get(method).size());

        assertTrue(mappingConfigurationsInfo.getMethodsUrls().get(method).contains(uri));

        assertEquals(1, mappingConfigurationsInfo.getRouters().size());
        assertTrue(mappingConfigurationsInfo.getRouters().containsKey(routerConfig.getId()));

        UriConfigHolder<RouterConfig> uriConfigHolder = mappingConfigurationsInfo.getRouters().get(routerConfig.getId());
        assertEquals(1, uriConfigHolder.getControllers().size());
        assertTrue(server.hasController(uriConfigHolder.getControllers().keySet().iterator().next()));

        assertEquals(1, uriConfigHolder.getUsedUrls().size());
        assertTrue(uriConfigHolder.getUsedUrls().contains(uri));

        BaseController baseController = uriConfigHolder.getControllers().get(uriConfigHolder.getControllers().keySet().iterator().next());
        assertEquals(RouterController.class, baseController.getClass());
    }

    @ParameterizedTest
    @MethodSource("provideAllUrisMethods")
    void initRouter_CantSaveYaml_UnregisterRouter(String uri, HttpMethod method) throws ConfigException {
        when(yamlConfigurator.isRouterExist(any())).thenReturn(false);
        when(yamlConfigurator.addRouter(any())).thenReturn(false);

        var subj = Mockito.spy(routerMappingConfigurator);

        RouterConfig routerConfig = new RouterConfig();
        routerConfig.setUri(uri);
        routerConfig.setToUrl(TEST_STATIC_URI_SLASH_IN_END);
        routerConfig.setMethod(method);
        subj.registerRouter(routerConfig);
        verify(subj).unregisterRouter(routerConfig.getId());
    }

    @ParameterizedTest
    @MethodSource("provideAllUrisMethods")
    void unregisterRouter_Ok(String uri, HttpMethod method) throws ConfigException {
        setUpYamlOk();
        RouterConfig routerConfig = new RouterConfig();
        routerConfig.setUri(uri);
        routerConfig.setToUrl(TEST_STATIC_URI_SLASH_IN_END);
        routerConfig.setMethod(method);
        routerMappingConfigurator.registerRouter(routerConfig);

        when(yamlConfigurator.isRouterExist(routerConfig)).thenReturn(true);

        UriConfigHolder<RouterConfig> uriConfigHolder = mappingConfigurationsInfo.getRouters().get(routerConfig.getId());
        assertEquals(1, uriConfigHolder.getControllers().size());
        assertTrue(server.hasController(uriConfigHolder.getControllers().keySet().iterator().next()));
        BaseUriConfig routerConfigUriConfigHolder = uriConfigHolder.getControllers().keySet().iterator().next();

        routerMappingConfigurator.unregisterRouter(routerConfig.getId());

        assertTrue(mappingConfigurationsInfo.getMethodsUrls().containsKey(method));
        assertEquals(0, mappingConfigurationsInfo.getMethodsUrls().get(method).size());
        assertEquals(0, mappingConfigurationsInfo.getRouters().size());
        assertFalse(server.hasController(routerConfigUriConfigHolder));
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
