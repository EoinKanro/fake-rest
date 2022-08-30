package io.github.ivanrosw.fakerest.core.conf;

import io.github.ivanrosw.fakerest.core.controller.BaseController;
import io.github.ivanrosw.fakerest.core.controller.RouterController;
import io.github.ivanrosw.fakerest.core.model.GeneratorPattern;
import io.github.ivanrosw.fakerest.core.model.RouterConfig;
import io.github.ivanrosw.fakerest.core.model.UriConfigHolder;
import io.github.ivanrosw.fakerest.core.utils.RestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configurator that register and unregister routers
 */
@Slf4j
@Component
public class RouterMappingConfigurator extends MappingConfigurator {

    @Autowired
    private RestClient restClient;

    /**
     * Method to init and run router controller
     * Can be called from api
     *
     * @param conf - config with all necessary info to init router
     * @throws ConfigException - if config don't contain all necessary info or url already registered
     */
    public void registerRouter(RouterConfig conf) throws ConfigException {
        beforeInitRouterCheck(conf);

        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();
        RequestMappingInfo routerInfo = RequestMappingInfo
                .paths(conf.getUri())
                .methods(conf.getMethod())
                .build();

        RouterController routerController = new RouterController(conf, httpUtils, restClient);
        requestMappingInfo.put(routerInfo, routerController);
        usedUrls.add(conf.getUri());
        UriConfigHolder<RouterConfig> configHolder = new UriConfigHolder<>(conf, requestMappingInfo, usedUrls);

        registerMapping(configHolder);
        addUrls(configHolder);

        conf.setId(idGenerator.generateId(GeneratorPattern.SEQUENCE));
        mappingConfiguratorData.getRouters().put(conf.getId(), configHolder);

        if (!yamlConfigurator.isRouterExist(conf) && !yamlConfigurator.addRouter(conf)) {
            log.error("Cant save config to yaml. Method: [{}],  Urls:{}", conf.getMethod(), configHolder.getUsedUrls());
            unregisterRouter(conf.getId());
        } else {
            log.info("Registered router. Method: [{}],  Urls:{}", conf.getMethod(), configHolder.getUsedUrls());
        }
    }

    /**
     * Check configuration before init and run router
     *
     * @param conf - config with all necessary info to init controller
     * @throws ConfigException - if config don't contain all necessary info or url already registered
     */
    private void beforeInitRouterCheck(RouterConfig conf) throws ConfigException {
        if (conf.getUri() == null || conf.getUri().isEmpty() || conf.getToUrl() == null || conf.getToUrl().isEmpty()) {
            throw new ConfigException("Router: Uri and toUrl must be not blank");
        }
        if (conf.getUri().equals(conf.getToUrl())) {
            throw new ConfigException("Router: Uri and toUrl can't be equals");
        }
        if (conf.getMethod() == null) {
            throw new ConfigException("Router: Method must be specified");
        }
        if (conf.getToUrl().contains("\\")) {
            conf.setToUrl(conf.getToUrl().replace("\\", "/"));
        }

        List<String> urls = mappingConfiguratorData.getMethodsUrls().computeIfAbsent(conf.getMethod(), key -> new ArrayList<>());
        if (urls.contains(conf.getUri())) {
            throw new ConfigException(String.format("Router: Duplicated urls: %s", conf.getUri()));
        }
    }

    /**
     * Delete and stop routers
     * Can be called from api
     *
     * @param id - id of configuration
     * @throws ConfigException - if configuration with id not exist
     */
    public void unregisterRouter(String id) throws ConfigException {
        if (!mappingConfiguratorData.getRouters().containsKey(id)) {
            throw new ConfigException(String.format("Router with id [%s] not exist", id));
        }
        UriConfigHolder<RouterConfig> configHolder = mappingConfiguratorData.getRouters().get(id);

        if (yamlConfigurator.isRouterExist(configHolder.getConfig())) {
            yamlConfigurator.deleteRouter(configHolder.getConfig());
        }

        unregisterMapping(configHolder);
        List<String> urls = mappingConfiguratorData.getMethodsUrls().get(configHolder.getConfig().getMethod());
        urls.removeAll(configHolder.getUsedUrls());
        mappingConfiguratorData.getRouters().remove(id);
        log.info("Unregistered router. Method: [{}], Urls: {}", configHolder.getConfig().getMethod(), configHolder.getUsedUrls());
    }
}
