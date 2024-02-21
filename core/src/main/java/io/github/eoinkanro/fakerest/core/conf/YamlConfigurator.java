package io.github.eoinkanro.fakerest.core.conf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.model.BaseUriConfig;
import io.github.eoinkanro.fakerest.core.model.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.RouterConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Class works with application.yml file
 * It can create or delete controllers or routers configurations in file
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class YamlConfigurator {

    private static final String YAML_NAME = "application.yml";

    private static final String REST_PARAM = "rest";
    private static final String CONTROLLERS_PARAM = "controllers";
    private static final String ROUTERS_PARAM = "routers";
    private static final String ID_PARAM = "id";
    private static final String URI_PARAM = "uri";
    private static final String METHOD_PARAM = "method";

    private static final String CONTROLLER_PARAM = "controller";
    private static final String ROUTER_PARAM = "router";

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private final ConfigurableEnvironment env;

    //CONTROLLER

    /**
     * Add controller config to file
     *
     * @param conf - controller config
     * @return - config added
     */
    boolean addController(ControllerConfig conf) {
        return addConfig(conf, CONTROLLERS_PARAM);
    }

    /**
     * Delete controller config from file
     *
     * @param conf - controller config
     */
    void deleteController(ControllerConfig conf) {
        deleteConfig(conf, CONTROLLERS_PARAM);
    }

    /**
     * Check is controller config exists in file
     *
     * @param conf - controller config
     * @return - is config exist
     */
    boolean isControllerExist(ControllerConfig conf) {
        return isConfigExist(conf, CONTROLLERS_PARAM);
    }

    //ROUTER

    /**
     * Add router config to file
     *
     * @param conf - router config
     * @return - config added
     */
    boolean addRouter(RouterConfig conf) {
        return addConfig(conf, ROUTERS_PARAM);
    }

    /**
     * Delete router config from file
     *
     * @param conf - router config
     */
    void deleteRouter(RouterConfig conf) {
        deleteConfig(conf, ROUTERS_PARAM);
    }

    /**
     * Check is router config exists in file
     *
     * @param conf - router config
     * @return - is config exist
     */
    boolean isRouterExist(RouterConfig conf) {
        return isConfigExist(conf, ROUTERS_PARAM);
    }

    //GENERALE

    /**
     * Add new config to file
     *
     * @param conf - config
     * @param keyParam - controller or router area
     * @return - config added
     */
    private boolean addConfig(BaseUriConfig conf, String keyParam) {
        try {
            ObjectNode yaml = getConfig();
            ArrayNode configs = getControllersOrRouters(yaml, keyParam);
            ObjectNode jsonConf = JsonUtils.toObjectNode(conf);
            jsonConf.remove(ID_PARAM);
            configs.add(jsonConf);
            writeConfig(yaml);
            log.info("Added {} to config. Method: {}, uri: {}", conf instanceof ControllerConfig ? CONTROLLER_PARAM : ROUTER_PARAM,
                    conf.getMethod(),
                    conf.getUri());
            return true;
        } catch (Exception e) {
            log.error("Error while saving config", e);
            return false;
        }
    }

    /**
     * Delete config from file
     *
     * @param conf - config
     * @param keyParam - controller or router area
     */
    private void deleteConfig(BaseUriConfig conf, String keyParam) {
        ObjectNode yaml = getConfig();
        ArrayNode configs = getControllersOrRouters(yaml, keyParam);

        boolean isDeleted = false;
        for (int i = 0; i < configs.size(); i++) {
            JsonNode configsConf = configs.get(i);
            String configsConfUri = JsonUtils.getString(configsConf, URI_PARAM);
            String configsConfMethod = JsonUtils.getString(configsConf, METHOD_PARAM);

            if (conf.getMethod().toString().equals(configsConfMethod) && conf.getUri().equals(configsConfUri)) {
                configs.remove(i);
                isDeleted = true;
                break;
            }
        }

        if (isDeleted) {
            try {
                writeConfig(yaml);
                log.info("Deleted {} from config. Method: {}, uri: {}", conf instanceof ControllerConfig ? CONTROLLER_PARAM : ROUTER_PARAM,
                        conf.getMethod(),
                        conf.getUri());
            } catch (Exception e) {
                log.error("Error while deleting config", e);
            }
        }
    }

    /**
     * Check is config exists in file
     *
     * @param conf - config
     * @param keyParam - controller or router area
     * @return - is config exist
     */
    private boolean isConfigExist(BaseUriConfig conf, String keyParam) {
        ObjectNode yaml = getConfig();
        ArrayNode configs = getControllersOrRouters(yaml, keyParam);

        boolean result = false;
        for (int i = 0; i < configs.size(); i++) {
            JsonNode configsConf = configs.get(i);
            String configsConfUri = JsonUtils.getString(configsConf, URI_PARAM);
            String configsConfMethod = JsonUtils.getString(configsConf, METHOD_PARAM);

            if (conf.getMethod().toString().equals(configsConfMethod) && conf.getUri().equals(configsConfUri)) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * Get array of configurations from config file
     *
     * @param yaml - configuration file
     * @param key - controller or router area
     * @return - array of configurations
     */
    private ArrayNode getControllersOrRouters(ObjectNode yaml, String key) {
        ObjectNode rest = getRest(yaml);

        ArrayNode value;
        if (rest.has(key)) {
            value = JsonUtils.getArray(rest, key);
        } else {
            value = JsonUtils.createArray();
            JsonUtils.putJson(rest, key, value);
        }
        return value;
    }

    /**
     * Get controllers and routers config array from config file
     *
     * @param yaml - config
     * @return - controllers and routers config array from config file
     */
    private ObjectNode getRest(ObjectNode yaml) {
        ObjectNode rest;
        if (yaml.has(REST_PARAM)) {
            rest = JsonUtils.getJson(yaml, REST_PARAM);
        } else {
            rest = JsonUtils.createJson();
            JsonUtils.putJson(yaml, REST_PARAM, rest);
        }
        return rest;
    }

    //FILE

    /**
     * Get configuration
     *
     * @return - configuration file in json format
     */
    private ObjectNode getConfig() {
        ObjectNode conf;
        try {
            conf = mapper.readValue(getConfigFile(), ObjectNode.class);
        } catch (Exception e) {
            conf = JsonUtils.createJson();
            log.warn("Error while parse configuration file. Creating new one", e);
        }
        return conf;
    }

    /**
     * Read configuration file
     *
     * @return - configuration file
     * @throws IOException - exception from {@link #getYamlPath()}
     */
    private File getConfigFile() throws IOException {
        String yamlPath = getYamlPath();
        log.info("Getting file {}", yamlPath);
        return new File(yamlPath);
    }

    /**
     * Get path to configuration file
     *
     * @return - path to configuration file
     * @throws UnsupportedEncodingException - if can't decode string
     */
    private String getYamlPath() {
        String projectPath = System.getProperty("user.dir");
        String decodedPath = URLDecoder.decode(projectPath, StandardCharsets.UTF_8);
        MutablePropertySources propertySources = env.getPropertySources();

        String result = null;
        for (PropertySource<?> source : propertySources) {
            String sourceName = source.getName();
            if (sourceName.contains("Config resource '")) {
                result = getYamlPath(decodedPath, sourceName);
                if (result != null) {
                    break;
                }
            }
        }

        return result == null ? decodedPath + File.separator + YAML_NAME : result;
    }

    /**
     * Get path to loaded configuration file
     *
     * @param projectFolder - full path to project folder
     * @param sourceName - spring source name of loaded configuration
     * @return - path to configuration file or null
     */
    private String getYamlPath(String projectFolder, String sourceName) {
        String result = null;
        String filePath = sourceName.substring(sourceName.indexOf("[") + 1, sourceName.indexOf("]"));

        if (sourceName.contains("classpath")) {
            URL classPath = getClass().getClassLoader().getResource(filePath);
            if (classPath != null) filePath = new File(classPath.getFile()).getAbsolutePath();
        }

        if (!Paths.get(filePath).isAbsolute()) {
            filePath = projectFolder + File.separator + filePath;
        }

        File file = new File(filePath);
        if (file.exists() && file.canWrite()) {
            result = filePath;
        }
        return result;
    }

    /**
     * Write data to configuration file
     *
     * @param conf - configuration file in json format
     */
    private void writeConfig(ObjectNode conf) throws IOException {
        File file = getConfigFile();
        log.info("Writing file {}", file.getAbsolutePath());
        mapper.writer().writeValue(file, conf);
    }
}
