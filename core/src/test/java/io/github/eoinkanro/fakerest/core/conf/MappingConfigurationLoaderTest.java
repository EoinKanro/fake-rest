package io.github.eoinkanro.fakerest.core.conf;

import io.github.eoinkanro.fakerest.core.FakeRestApplication;
import io.github.eoinkanro.fakerest.core.model.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.RouterConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = FareRestTestApplication.class)
@TestPropertySource(properties = {"spring.config.location = classpath:configuration-tests.yml"})
class MappingConfigurationLoaderTest {

    @SpyBean
    private MappingConfigurationLoader mappingConfigurationLoader;

    @MockBean
    private ControllerMappingConfigurator controllerMappingConfigurator;

    @MockBean
    private RouterMappingConfigurator routerMappingConfigurator;

    @Test
    void initConfigFile_ConfigurationsLoaded() throws NoSuchFieldException, IllegalAccessException {
        Field controllersField = MappingConfigurationLoader.class.getDeclaredField("controllers");
        controllersField.setAccessible(true);
        List<ControllerConfig> controllers = (List<ControllerConfig>) controllersField.get(mappingConfigurationLoader);
        assertEquals(1, controllers.size());
        controllersField.setAccessible(false);

        Field routersField = MappingConfigurationLoader.class.getDeclaredField("routers");
        routersField.setAccessible(true);
        List<RouterConfig> routers = (List<RouterConfig>) routersField.get(mappingConfigurationLoader);
        assertEquals(1, routers.size());
        routersField.setAccessible(false);
    }

    @Test
    void init_InitsCalled() throws ConfigException {
        verify(controllerMappingConfigurator, times(1)).registerController(any());
        verify(routerMappingConfigurator, times(1)).registerRouter(any());
    }

}
