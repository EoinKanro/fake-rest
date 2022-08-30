package io.github.ivanrosw.fakerest.core.controller;

import io.github.ivanrosw.fakerest.core.model.*;
import io.github.ivanrosw.fakerest.core.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Component
public class TestControllersFabric {

    @Autowired
    private ControllerData controllerData;
    @Autowired
    private JsonUtils jsonUtils;
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private SystemUtils systemUtils;
    @Autowired
    private RestClient restClient;

    public ReadController createStaticReadController(String uri, RequestMethod method, String answer, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, answer, delayMs,
                false, null);
        return createReadController(config, ControllerSaveInfoMode.STATIC);
    }

    public ReadController createCollectionAllReadController(String uri, RequestMethod method, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                false, null);
        return createReadController(config, ControllerSaveInfoMode.COLLECTION_ALL);
    }

    public ReadController createCollectionOneReadController(String uri, RequestMethod method, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                false, null);
        return createReadController(config, ControllerSaveInfoMode.COLLECTION_ONE);
    }

    public DeleteController createStaticDeleteController(String uri, RequestMethod method, String answer, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.DELETE, answer, delayMs,
                false, null);
        return createDeleteController(config, ControllerSaveInfoMode.STATIC);
    }

    public DeleteController createCollectionOneDeleteController(String uri, RequestMethod method, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.DELETE, null, delayMs,
                false, null);
        return createDeleteController(config, ControllerSaveInfoMode.COLLECTION_ONE);
    }

    public CreateController createStaticCreateController(String uri, RequestMethod method, String answer, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, answer, delayMs,
                false, null);
        return createCreateController(config, ControllerSaveInfoMode.STATIC);
    }

    public CreateController createCollectionOneCreateController(String uri, RequestMethod method, long delayMs, boolean generateId) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                generateId, null);
        return createCreateController(config, ControllerSaveInfoMode.COLLECTION_ONE);
    }

    public UpdateController createStaticUpdateController(String uri, RequestMethod method, String answer, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, answer, delayMs,
                false, null);
        return createUpdateController(config, ControllerSaveInfoMode.STATIC);
    }

    public UpdateController createCollectionOneUpdateController(String uri, RequestMethod method, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                false, null);
        return createUpdateController(config, ControllerSaveInfoMode.COLLECTION_ONE);
    }

    public RouterController createRouterController(String fromUri, String toUri, RequestMethod method) {
        RouterConfig routerConfig = createRouterConfig(fromUri, toUri, method);
        return new RouterController(routerConfig, httpUtils, restClient);
    }

    public GroovyController createGroovyController(String uri, RequestMethod method, long delayMs, String groovyScript) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                false, groovyScript);
        return new GroovyController(config, controllerData, jsonUtils, httpUtils, systemUtils);
    }

    private ControllerConfig createControllerConfig(String uri, RequestMethod method, ControllerFunctionMode functionMode,
                                                    String answer, long delayMs, boolean generateId, String groovyScript) {
        ControllerConfig config = new ControllerConfig();
        config.setUri(uri);
        config.setMethod(method);
        config.setFunctionMode(functionMode);
        config.setAnswer(answer);
        config.setDelayMs(delayMs);
        config.setGenerateId(generateId);
        config.setGroovyScript(groovyScript);
        List<String> idParams = httpUtils.getIdParams(uri);
        config.setIdParams(idParams);
        idParams.forEach(id -> config.getGenerateIdPatterns().put(id, GeneratorPattern.SEQUENCE));
        return config;
    }

    private RouterConfig createRouterConfig(String uri, String toUrl, RequestMethod method) {
        RouterConfig config = new RouterConfig();
        config.setUri(uri);
        config.setToUrl(toUrl);
        config.setMethod(method);
        return config;
    }

    private ReadController createReadController(ControllerConfig config, ControllerSaveInfoMode saveInfoMode) {
        return ReadController.builder()
                .saveInfoMode(saveInfoMode)
                .controllerData(controllerData)
                .controllerConfig(config)
                .jsonUtils(jsonUtils)
                .httpUtils(httpUtils)
                .systemUtils(systemUtils).build();
    }

    private DeleteController createDeleteController(ControllerConfig config, ControllerSaveInfoMode saveInfoMode) {
        return DeleteController.builder()
                .saveInfoMode(saveInfoMode)
                .controllerData(controllerData)
                .controllerConfig(config)
                .jsonUtils(jsonUtils)
                .httpUtils(httpUtils)
                .systemUtils(systemUtils).build();
    }

    private CreateController createCreateController(ControllerConfig config, ControllerSaveInfoMode saveInfoMode) {
        return CreateController.builder()
                .saveInfoMode(saveInfoMode)
                .controllerData(controllerData)
                .controllerConfig(config)
                .jsonUtils(jsonUtils)
                .httpUtils(httpUtils)
                .systemUtils(systemUtils)
                .idGenerator(new IdGenerator()).build();
    }

    private UpdateController createUpdateController(ControllerConfig config, ControllerSaveInfoMode saveInfoMode) {
        return UpdateController.builder()
                .saveInfoMode(saveInfoMode)
                .controllerData(controllerData)
                .controllerConfig(config)
                .jsonUtils(jsonUtils)
                .httpUtils(httpUtils)
                .systemUtils(systemUtils).build();
    }

}
