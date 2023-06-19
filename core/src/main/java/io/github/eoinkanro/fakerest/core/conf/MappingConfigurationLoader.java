package io.github.eoinkanro.fakerest.core.conf;

import io.github.eoinkanro.fakerest.core.model.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.RouterConfig;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class that load configuration from file and init
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "rest")
public class MappingConfigurationLoader {

    @Setter(AccessLevel.PACKAGE)
    private List<ControllerConfig> controllers;

    @Setter(AccessLevel.PACKAGE)
    private List<RouterConfig> routers;

    @Autowired
    private ControllerMappingConfigurator controllersConfigurator;
    @Autowired
    private RouterMappingConfigurator routersConfigurator;

    @PostConstruct
    private void init() throws ConfigException {
        initControllers();
        initRouters();
        controllersConfigurator.printUrls();
    }

    private void initControllers() throws ConfigException {
        if (controllers != null) {
            for (ControllerConfig conf : controllers) {
                controllersConfigurator.registerController(conf);
            }
        }
    }

    private void initRouters() throws ConfigException {
        if (routers != null) {
            for (RouterConfig conf : routers) {
                routersConfigurator.registerRouter(conf);
            }
        }
    }
}
