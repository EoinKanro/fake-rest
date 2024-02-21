package io.github.eoinkanro.fakerest.core.conf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.controller.*;
import io.github.eoinkanro.fakerest.core.model.*;
import io.github.eoinkanro.fakerest.core.utils.HttpUtils;
import io.github.eoinkanro.fakerest.core.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configurator that register and unregister controllers
 */
@Slf4j
@Component
public class ControllerMappingConfigurator extends MappingConfigurator {

    private final ControllerData controllerData;

    @Autowired
    public ControllerMappingConfigurator(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping,
                                         MappingConfiguratorData mappingConfiguratorData,
                                         YamlConfigurator yamlConfigurator,
                                         ControllerData controllerData) {
        super(handlerMapping, mappingConfiguratorData, yamlConfigurator);
        this.controllerData = controllerData;
    }

    /**
     * Method to init and run controller
     * Can be called from api
     *
     * @param conf - config with all necessary info to init controller
     * @throws ConfigException - if config don't contain all necessary info or url already registered
     */
    public void registerController(ControllerConfig conf) throws ConfigException {
        beforeInitControllerCheckBase(conf);
        List<String> idParams = HttpUtils.getIdParams(conf.getUri());
        ControllerSaveInfoMode mode = identifyMode(conf, idParams);
        beforeInitControllerCheckMode(conf, mode);

        if (mode == ControllerSaveInfoMode.COLLECTION) conf.setIdParams(idParams);

        UriConfigHolder<ControllerConfig> configHolder = switch (conf.getFunctionMode()) {
            case READ -> createReadControllerHolder(conf, mode);
            case CREATE -> createCreateControllerHolder(conf, mode);
            case UPDATE -> createUpdateControllerHolder(conf, mode);
            case DELETE -> createDeleteControllerHolder(conf, mode);
            case GROOVY -> createGroovyControllerHolder(conf);
            default ->
                    throw new ConfigException(String.format("Controller: Function mode [%s] is not supported", conf.getFunctionMode()));
        };

        registerMapping(configHolder);
        conf.setId(idGenerator.generateId(GeneratorPattern.SEQUENCE));
        if (mode == ControllerSaveInfoMode.COLLECTION) loadCollectionAnswerData(conf);

        addUrls(configHolder);
        mappingConfiguratorData.getControllers().put(conf.getId(), configHolder);
        if (!yamlConfigurator.isControllerExist(conf) && !yamlConfigurator.addController(conf)) {
            log.error("Cant save config to yaml. Method: [{}],  Urls:{}", conf.getMethod(), configHolder.getUsedUrls());
            unregisterController(conf.getId());
        } else {
            log.info("Registered controllers. Method: [{}],  Urls:{}", conf.getMethod(), configHolder.getUsedUrls());
        }
    }

    /**
     * Check configuration before init and run controller
     *
     * @param conf - config with all necessary info to init controller
     * @throws ConfigException - if config don't contain all necessary info or url already registered
     */
    private void beforeInitControllerCheckBase(ControllerConfig conf) throws ConfigException {
        if (conf.getUri() == null || conf.getUri().isEmpty()) {
            throw new ConfigException("Controller: Uri must be not blank");
        }
        if (conf.getMethod() == null) {
            throw new ConfigException("Controller: Method must be specified");
        }
        if (conf.getFunctionMode() == null) {
            throw new ConfigException("Controller: function mode must be specified");
        }
        if (conf.getFunctionMode() == ControllerFunctionMode.GROOVY &&
            (conf.getGroovyScript() == null || conf.getGroovyScript().isEmpty())) {
            throw new ConfigException("Controller: groovy script must be specified");
        }
    }

    /**
     * Check configuration before init and run controller after init mode
     *
     * @param conf - config with all necessary info to init controller
     * @param mode - static or collection mode
     * @throws ConfigException - if config don't contain all necessary info or url already registered
     */
    private void beforeInitControllerCheckMode(ControllerConfig conf, ControllerSaveInfoMode mode) throws ConfigException {
        List<String> urls = mappingConfiguratorData.getMethodsUrls().computeIfAbsent(conf.getMethod(), key -> new ArrayList<>());
        if (urls.contains(conf.getUri()) ||
                (conf.getFunctionMode() == ControllerFunctionMode.READ &&
                 mode == ControllerSaveInfoMode.COLLECTION &&
                 urls.contains(HttpUtils.getBaseUri(conf.getUri())))) {
            throw new ConfigException(String.format("Controller: Duplicated urls: %s", conf.getUri()));
        }
    }

    /**
     * Identify save info mode base on identify idParams
     *
     * @param conf - config of controller
     * @param idParams - id params from url if it set
     * @return - groovy, static or collection mode
     */
    private ControllerSaveInfoMode identifyMode(ControllerConfig conf, List<String> idParams) {
        if (conf.getFunctionMode() == ControllerFunctionMode.GROOVY) {
            return ControllerSaveInfoMode.GROOVY;
        }
        return idParams.isEmpty() ? ControllerSaveInfoMode.STATIC : ControllerSaveInfoMode.COLLECTION;
    }

    /**
     * Create config holder for read controller
     *
     * @param conf - config with all necessary info to init controller
     * @param mode - static or collection mode
     * @return - config holder that haven't ran yet
     */
    private UriConfigHolder<ControllerConfig> createReadControllerHolder(ControllerConfig conf, ControllerSaveInfoMode mode) {
        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();

        if (mode == ControllerSaveInfoMode.COLLECTION) {
            String baseUri = HttpUtils.getBaseUri(conf.getUri());
            RequestMappingInfo getAllMappingInfo = RequestMappingInfo
                    .paths(baseUri)
                    .methods(conf.getMethod())
                    .build();

            FakeController readAllController = ReadController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ALL)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .build();
            requestMappingInfo.put(getAllMappingInfo, readAllController);
            usedUrls.add(baseUri);

            RequestMappingInfo readOneMappingInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController getOneController = ReadController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .build();
            requestMappingInfo.put(readOneMappingInfo, getOneController);
            usedUrls.add(conf.getUri());

        } else {
            RequestMappingInfo readStaticMappingInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController getStaticController =  ReadController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .build();
            requestMappingInfo.put(readStaticMappingInfo, getStaticController);
            usedUrls.add(conf.getUri());
        }
        return new UriConfigHolder<>(conf,requestMappingInfo, usedUrls);
    }

    /**
     * Create config holder for create controller
     *
     * @param conf - config with all necessary info to init controller
     * @param mode - static or collection mode
     * @return - config holder that haven't ran yet
     */
    private UriConfigHolder<ControllerConfig> createCreateControllerHolder(ControllerConfig conf, ControllerSaveInfoMode mode) {
        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();
        IdGenerator idGenerator = new IdGenerator();

        if (mode == ControllerSaveInfoMode.COLLECTION) {
            String baseUri = HttpUtils.getBaseUri(conf.getUri());
            RequestMappingInfo createOneInfo = RequestMappingInfo
                    .paths(baseUri)
                    .methods(conf.getMethod())
                    .build();

            FakeController createOneController = CreateController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .idGenerator(idGenerator)
                    .build();
            requestMappingInfo.put(createOneInfo, createOneController);
            usedUrls.add(baseUri);

        } else {
            RequestMappingInfo createStaticInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController createStaticController = CreateController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .idGenerator(idGenerator)
                    .build();
            requestMappingInfo.put(createStaticInfo, createStaticController);
            usedUrls.add(conf.getUri());
        }
        return new UriConfigHolder<>(conf, requestMappingInfo, usedUrls);
    }

    /**
     * Create config holder for update controller
     *
     * @param conf - config with all necessary info to init controller
     * @param mode - static or collection mode
     * @return - config holder that haven't ran yet
     */
    private UriConfigHolder<ControllerConfig> createUpdateControllerHolder(ControllerConfig conf, ControllerSaveInfoMode mode) {
        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();

        if (mode == ControllerSaveInfoMode.COLLECTION) {
            RequestMappingInfo updateOneInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController updateOneController = UpdateController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .build();
            requestMappingInfo.put(updateOneInfo, updateOneController);
            usedUrls.add(conf.getUri());

        } else {
            RequestMappingInfo updateStaticInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController updateStaticController = UpdateController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .build();
            requestMappingInfo.put(updateStaticInfo, updateStaticController);
            usedUrls.add(conf.getUri());
        }
        return new UriConfigHolder<>(conf, requestMappingInfo, usedUrls);
    }

    /**
     * Create config holder for delete controller
     *
     * @param conf - config with all necessary info to init controller
     * @param mode - static or collection mode
     * @return - config holder that haven't ran yet
     */
    private UriConfigHolder<ControllerConfig> createDeleteControllerHolder(ControllerConfig conf, ControllerSaveInfoMode mode) {
        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();

        if (mode == ControllerSaveInfoMode.COLLECTION) {
            RequestMappingInfo deleteOneInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController deleteOneController = DeleteController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .build();
            requestMappingInfo.put(deleteOneInfo, deleteOneController);
            usedUrls.add(conf.getUri());

        } else {
            RequestMappingInfo deleteStaticInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController deleteStaticController = DeleteController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .build();
            requestMappingInfo.put(deleteStaticInfo, deleteStaticController);
            usedUrls.add(conf.getUri());
        }
        return new UriConfigHolder<>(conf, requestMappingInfo, usedUrls);
    }

    /**
     * Create config holder for groovy controller
     *
     * @param conf - config with all necessary info to init controller
     * @return - config holder that haven't ran yet
     */
    private UriConfigHolder<ControllerConfig> createGroovyControllerHolder(ControllerConfig conf) {
        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();

        RequestMappingInfo groovyInfo = RequestMappingInfo
                .paths(conf.getUri())
                .methods(conf.getMethod())
                .build();

        GroovyController groovyController = GroovyController.builder()
                .controllerData(controllerData)
                .controllerConfig(conf)
                .build();

        requestMappingInfo.put(groovyInfo, groovyController);
        usedUrls.add(conf.getUri());

        return new UriConfigHolder<>(conf, requestMappingInfo, usedUrls);
    }

    /**
     * Load data from config to controller's collection data
     *
     * @param conf - config with all necessary info to init controller
     */
    private void loadCollectionAnswerData(ControllerConfig conf) {
        if (conf.getAnswer() != null && (conf.getAnswer().contains("{") || conf.getAnswer().contains("["))) {
            JsonNode answer = JsonUtils.toJsonNode(conf.getAnswer());

            if (answer instanceof ArrayNode answerArray) {
                answerArray.forEach(jsonNode -> addAnswerData(conf, (ObjectNode) jsonNode));
            } else if (answer instanceof ObjectNode answerObject) {
                addAnswerData(conf, answerObject);
            } else {
                log.warn("Cant put data [{}] to collection [{}]. Its not json", answer, conf.getUri());
            }
        }
    }

    /**
     * Add data to controller's collection data
     *
     * @param conf - config with all necessary info to init controller
     * @param data - json to add
     */
    private void addAnswerData(ControllerConfig conf, ObjectNode data) {
        String key = controllerData.buildKey(data, conf.getIdParams());
        controllerData.putData(conf.getUri(), key, data);
    }

    /**
     * Delete and stop controllers
     * Can be called from api
     *
     * @param id - id of configuration
     * @throws ConfigException - if configuration with id not exist
     */
    public void unregisterController(String id) throws ConfigException {
        if (!mappingConfiguratorData.getControllers().containsKey(id)) {
            throw new ConfigException(String.format("Controller with id [%s] not exist", id));
        }
        UriConfigHolder<ControllerConfig> configHolder = mappingConfiguratorData.getControllers().get(id);
        ControllerConfig conf = configHolder.getConfig();

        if (yamlConfigurator.isControllerExist(conf)) {
            yamlConfigurator.deleteController(conf);
        }

        unregisterMapping(configHolder);
        List<String> urls = mappingConfiguratorData.getMethodsUrls().get(conf.getMethod());
        urls.removeAll(configHolder.getUsedUrls());
        mappingConfiguratorData.getControllers().remove(id);

        deleteControllerData(conf);
        log.info("Unregistered controllers. Method: [{}], Urls: {}", configHolder.getConfig().getMethod(), configHolder.getUsedUrls());
    }

    /**
     * Delete controller data if no controllers with same uri
     *
     * @param conf - controller configuration
     */
    private void deleteControllerData(ControllerConfig conf) {
        ControllerSaveInfoMode mode = identifyMode(conf, conf.getIdParams());
        if (mode == ControllerSaveInfoMode.COLLECTION) {
            boolean isFound = false;

            for(UriConfigHolder<ControllerConfig> configHolder : mappingConfiguratorData.getControllers().values()) {
                if (!configHolder.getConfig().getId().equals(conf.getId()) &&
                     configHolder.getConfig().getUri().equals(conf.getUri())) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                controllerData.deleteAllData(conf.getUri());
            }
        }
    }
}
