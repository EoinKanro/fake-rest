package io.github.eoinkanro.fakerest.core.conf.file;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.conf.server.controller.ControllerMappingConfigurator;
import io.github.eoinkanro.fakerest.core.conf.server.controller.RouterMappingConfigurator;
import io.github.eoinkanro.fakerest.core.conf.server.MappingConfigurationsInfo;
import io.github.eoinkanro.fakerest.core.model.conf.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.conf.RouterConfig;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class that load configuration from file and init
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class YamlStartupLoader {

    private final YamlConfigurator yamlConfigurator;
    private final MappingConfigurationsInfo mappingConfigurationsInfo;

    private final RouterMappingConfigurator routersConfigurator;
    private final ControllerMappingConfigurator controllersConfigurator;

    public void loadConfiguration() {
        initControllers();
        initRouters();
        mappingConfigurationsInfo.printUrls();
    }

    private void initControllers() {
        ArrayNode controllers = yamlConfigurator.getControllers();
        controllers.forEach(json -> {
            try {
                ControllerConfig controllerConfig = JsonUtils.toObject(json.toString(), ControllerConfig.class);
                controllersConfigurator.registerController(controllerConfig);
            } catch (Exception e) {
                log.error("Can't load controller config from yaml", e);
            }
        });
    }

    private void initRouters() {
        ArrayNode routers = yamlConfigurator.getRouters();
        routers.forEach(json -> {
            try {
                RouterConfig routerConfig = JsonUtils.toObject(json.toString(), RouterConfig.class);
                routersConfigurator.registerRouter(routerConfig);
            } catch (Exception e) {
                log.error("Can't load controller config from yaml", e);
            }
        });
    }
}
