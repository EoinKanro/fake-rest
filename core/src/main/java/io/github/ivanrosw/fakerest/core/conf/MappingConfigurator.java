package io.github.ivanrosw.fakerest.core.conf;

import io.github.ivanrosw.fakerest.core.controller.BaseController;
import io.github.ivanrosw.fakerest.core.model.UriConfigHolder;
import io.github.ivanrosw.fakerest.core.utils.HttpUtils;
import io.github.ivanrosw.fakerest.core.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for Router and Controller Mapping Configurators
 */
@Slf4j
public abstract class MappingConfigurator {

    protected final IdGenerator idGenerator = new IdGenerator();

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    @Autowired
    protected MappingConfiguratorData mappingConfiguratorData;

    @Autowired
    protected HttpUtils httpUtils;
    @Autowired
    protected YamlConfigurator yamlConfigurator;

    /**
     * Register and run mappings
     *
     * @param configHolder - config holder with initiated request mappings and controllers
     * @throws ConfigException - if registration mapping failed
     */
    protected void registerMapping(UriConfigHolder<?> configHolder) throws ConfigException {
        try {
            for (Map.Entry<RequestMappingInfo, BaseController> entry : configHolder.getRequestMappingInfo().entrySet()) {
                handlerMapping.registerMapping(entry.getKey(), entry.getValue(),
                        BaseController.class.getMethod("handle", HttpServletRequest.class));
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
        configHolder.getRequestMappingInfo().keySet().forEach(requestMappingInfo -> handlerMapping.unregisterMapping(requestMappingInfo));
        if (log.isDebugEnabled()) log.debug("Uri [{}] with method [{}] unregistered", configHolder.getConfig().getUri(), configHolder.getConfig().getMethod());
    }

    /**
     * Add used urls to general collection
     *
     * @param configHolder - config holder with initiated request mappings and controllers
     */
    void addUrls(UriConfigHolder<?> configHolder) {
        List<String> urls = mappingConfiguratorData.getMethodsUrls().computeIfAbsent(configHolder.getConfig().getMethod(), key -> new ArrayList<>());
        urls.addAll(configHolder.getUsedUrls());
    }

    /**
     * Log active methods ands uris
     */
    public void printUrls() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("**** Configured URLs ****\n");

        mappingConfiguratorData.getMethodsUrls().forEach((method, urls) -> {
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
