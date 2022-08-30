package io.github.ivanrosw.fakerest.core.conf;

import io.github.ivanrosw.fakerest.core.model.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * It contains all information about active controllers and routers
 */
@Slf4j
@Component
@Getter(AccessLevel.PACKAGE)
public class MappingConfiguratorData {

    /**
     * Collection with active uris
     * Method - List of active uris
     */
    private Map<RequestMethod, List<String>> methodsUrls;
    /**
     * Collection with active controllers configs
     * Config id - controller config
     */
    private Map<String, UriConfigHolder<ControllerConfig>> controllers;
    /**
     * Collection with active routers configs
     * Config id - controller config
     */
    private Map<String, UriConfigHolder<RouterConfig>> routers;

    @PostConstruct
    private void init() {
        methodsUrls = new EnumMap<>(RequestMethod.class);
        controllers = new HashMap<>();
        routers = new HashMap<>();
    }

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
}
