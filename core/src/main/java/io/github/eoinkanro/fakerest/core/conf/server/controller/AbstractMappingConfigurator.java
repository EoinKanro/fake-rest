package io.github.eoinkanro.fakerest.core.conf.server.controller;

import io.github.eoinkanro.fakerest.core.conf.ConfigException;
import io.github.eoinkanro.fakerest.core.conf.server.MappingConfigurationsInfo;
import io.github.eoinkanro.fakerest.core.conf.file.YamlConfigurator;
import io.github.eoinkanro.fakerest.core.conf.server.UndertowServer;
import io.github.eoinkanro.fakerest.core.controller.BaseController;
import io.github.eoinkanro.fakerest.core.model.conf.BaseUriConfig;
import io.github.eoinkanro.fakerest.core.model.conf.UriConfigHolder;
import io.github.eoinkanro.fakerest.core.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for Router and Controller Mapping Configurators
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMappingConfigurator {

    protected final IdGenerator idGenerator = new IdGenerator();

    protected final MappingConfigurationsInfo mappingConfigurationsInfo;
    protected final YamlConfigurator yamlConfigurator;
    protected final UndertowServer server;

    /**
     * Register and run mappings
     *
     * @param configHolder - config holder with initiated request mappings and controllers
     * @throws ConfigException - if registration mapping failed
     */
    protected void registerMapping(UriConfigHolder<?> configHolder) throws ConfigException {
        try {
            for (Map.Entry<BaseUriConfig, BaseController> entry : configHolder.getControllers().entrySet()) {
                server.registerController(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            unregisterMapping(configHolder);
            throw new ConfigException(String.format("Error while register mapping with url [%s] and method [%s]",
                    configHolder.getConfig().getUri(), configHolder.getConfig().getMethod()), e);
        }
    }

    /**
     * Unregister and stop mappings
     *
     * @param configHolder - config holder with initiated request mappings and controllers
     */
    protected void unregisterMapping(UriConfigHolder<?> configHolder) {
        boolean isError = false;
        for (BaseUriConfig config : configHolder.getControllers().keySet()) {
            try {
                server.unregisterController(config);
            } catch (Exception e) {
                isError = true;
                log.error("Fatal error. Can't unregister controller", e);
            }
        }
        if (!isError) {
            log.debug("Uri [{}] with method [{}] unregistered", configHolder.getConfig().getUri(), configHolder.getConfig().getMethod());
        }
    }

    /**
     * Add used urls to general collection
     *
     * @param configHolder - config holder with initiated request mappings and controllers
     */
    void addUrls(UriConfigHolder<?> configHolder) {
        List<String> urls = mappingConfigurationsInfo.getMethodsUrls().computeIfAbsent(configHolder.getConfig().getMethod(), key -> new ArrayList<>());
        urls.addAll(configHolder.getUsedUrls());
    }

}
