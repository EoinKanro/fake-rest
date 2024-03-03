package io.github.eoinkanro.fakerest.core.conf.server;

import io.github.eoinkanro.fakerest.core.model.conf.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.conf.RouterConfig;
import io.github.eoinkanro.fakerest.core.model.conf.UriConfigHolder;
import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * It contains all information about active controllers and routers
 */
@Slf4j
@Getter
public class MappingConfigurationsInfo {

    /**
     * Collection with active uris
     * Method - List of active uris
     */
    private final Map<HttpMethod, List<String>> methodsUrls = new EnumMap<>(HttpMethod.class);
    /**
     * Collection with active controllers configs
     * Config id - controller config
     */
    private final Map<String, UriConfigHolder<ControllerConfig>> controllers = new HashMap<>();
    /**
     * Collection with active routers configs
     * Config id - controller config
     */
    private final Map<String, UriConfigHolder<RouterConfig>> routers = new HashMap<>();

    /**
     * Get copy of all controller's configs
     *
     * @return - copy of configs
     */
    public List<ControllerConfig> getAllControllersCopy() {
        List<ControllerConfig> copy = new ArrayList<>(controllers.size());
        controllers.values().forEach(conf -> copy.add(conf.getConfig().copy()));
        return copy;
    }

    /**
     * Get copy of controller's config by id
     *
     * @param id - id of config
     * @return - copy of config
     */
    public ControllerConfig getControllerCopy(String id) {
        return controllers.containsKey(id) ? controllers.get(id).getConfig().copy() : null;
    }

    /**
     * Get copy of all router's configs
     *
     * @return - copy of configs
     */
    public List<RouterConfig> getAllRoutersCopy() {
        List<RouterConfig> copy = new ArrayList<>(routers.size());
        routers.values().forEach(conf -> copy.add(conf.getConfig().copy()));
        return copy;
    }

    /**
     * Get copy of router's config by id
     *
     * @param id - id of config
     * @return - copy of config
     */
    public RouterConfig getRouterCopy(String id) {
        return routers.containsKey(id) ? routers.get(id).getConfig().copy() : null;
    }

    /**
     * Log active methods ands uris
     */
    public void printUrls() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("**** Configured URLs ****\n");

        methodsUrls.forEach((method, urls) -> {
            builder.append(method);
            builder.append(":\n");
            urls.forEach(url -> {
                builder.append("    ");
                builder.append(url);
                builder.append("\n");
            });
        });

        log.info(builder.toString());
    }
}
