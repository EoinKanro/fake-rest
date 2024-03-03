package io.github.eoinkanro.fakerest.core.conf.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.model.conf.BaseUriConfig;
import io.github.eoinkanro.fakerest.core.model.conf.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.conf.RouterConfig;
import io.github.eoinkanro.fakerest.core.utils.DefaultPropertiesUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Class works with application.yml file
 * It can create or delete controllers or routers configurations in file
 */
@Slf4j
public class YamlConfigurator {

    private static final String REST_PARAM = "rest";
    private static final String CONTROLLERS_PARAM = "controllers";
    private static final String ROUTERS_PARAM = "routers";
    private static final String ID_PARAM = "id";
    private static final String URI_PARAM = "uri";
    private static final String METHOD_PARAM = "method";

    private static final String CONTROLLER_PARAM = "controller";
    private static final String ROUTER_PARAM = "router";

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private File configFile = null;


    //CONTROLLER

    /**
     * Add controller config to file
     *
     * @param conf - controller config
     * @return - config added
     */
    public boolean addController(ControllerConfig conf) {
        return addConfig(conf, CONTROLLERS_PARAM);
    }

    /**
     * Delete controller config from file
     *
     * @param conf - controller config
     */
    public void deleteController(ControllerConfig conf) {
        deleteConfig(conf, CONTROLLERS_PARAM);
    }

    /**
     * Check is controller config exists in file
     *
     * @param conf - controller config
     * @return - is config exist
     */
    public boolean isControllerExist(ControllerConfig conf) {
        return isConfigExist(conf, CONTROLLERS_PARAM);
    }

    //ROUTER

    /**
     * Add router config to file
     *
     * @param conf - router config
     * @return - config added
     */
    public boolean addRouter(RouterConfig conf) {
        return addConfig(conf, ROUTERS_PARAM);
    }

    /**
     * Delete router config from file
     *
     * @param conf - router config
     */
    public void deleteRouter(RouterConfig conf) {
        deleteConfig(conf, ROUTERS_PARAM);
    }

    /**
     * Check is router config exists in file
     *
     * @param conf - router config
     * @return - is config exist
     */
    public boolean isRouterExist(RouterConfig conf) {
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

    public ArrayNode getControllers() {
        return getControllersOrRouters(CONTROLLERS_PARAM);
    }

    public ArrayNode getRouters() {
        return getControllersOrRouters(ROUTERS_PARAM);
    }

    private ArrayNode getControllersOrRouters(String key) {
        try {
            ObjectNode yaml = getConfig();
            return getControllersOrRouters(yaml, key);
        } catch (Exception e) {
            log.error("Can't load config", e);
            return JsonUtils.createArray();
        }
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
     */
    private File getConfigFile() {
        log.info("Getting config file {}", DefaultPropertiesUtils.getConfigFileName());
        if (configFile == null) {
            File projectDir = new File(URLDecoder.decode(System.getProperty("user.dir"), StandardCharsets.UTF_8));
            configFile = getFile(DefaultPropertiesUtils.getConfigFileName(), projectDir.listFiles());
        }

        if (configFile != null) {
            log.info("Got {}", configFile.getAbsolutePath());
        } else {
            log.info("Can't get config file");
        }
        return configFile;
    }

    private File getFile(String fileName, File[] files) {
        if (files == null || fileName == null) {
            return null;
        }

        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }

        File result = null;
        for (File file : files) {
            if (file.isDirectory()) {
                result = getFile(fileName, file.listFiles());
            }
            if (result != null) {
                if (!result.canRead() || !result.canWrite()) {
                    result = null;
                } else {
                    break;
                }
            }
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
