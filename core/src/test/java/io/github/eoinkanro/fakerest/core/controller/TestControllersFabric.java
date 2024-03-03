package io.github.eoinkanro.fakerest.core.controller;

import io.github.eoinkanro.fakerest.core.conf.server.controller.ControllerData;
import io.github.eoinkanro.fakerest.core.model.conf.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.conf.RouterConfig;
import io.github.eoinkanro.fakerest.core.model.enums.ControllerFunctionMode;
import io.github.eoinkanro.fakerest.core.model.enums.ControllerSaveInfoMode;
import io.github.eoinkanro.fakerest.core.model.enums.GeneratorPattern;
import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import io.github.eoinkanro.fakerest.core.utils.*;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class TestControllersFabric {

    private final ControllerData controllerData;
    private final RestClient restClient;

    public ReadController createStaticReadController(String uri, HttpMethod method, String answer, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, answer, delayMs,
                false, null);
        return createReadController(config, ControllerSaveInfoMode.STATIC);
    }

    public ReadController createCollectionAllReadController(String uri, HttpMethod method, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                false, null);
        return createReadController(config, ControllerSaveInfoMode.COLLECTION_ALL);
    }

    public ReadController createCollectionOneReadController(String uri, HttpMethod method, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                false, null);
        return createReadController(config, ControllerSaveInfoMode.COLLECTION_ONE);
    }

    public DeleteController createStaticDeleteController(String uri, HttpMethod method, String answer, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.DELETE, answer, delayMs,
                false, null);
        return createDeleteController(config, ControllerSaveInfoMode.STATIC);
    }

    public DeleteController createCollectionOneDeleteController(String uri, HttpMethod method, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.DELETE, null, delayMs,
                false, null);
        return createDeleteController(config, ControllerSaveInfoMode.COLLECTION_ONE);
    }

    public CreateController createStaticCreateController(String uri, HttpMethod method, String answer, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, answer, delayMs,
                false, null);
        return createCreateController(config, ControllerSaveInfoMode.STATIC);
    }

    public CreateController createCollectionOneCreateController(String uri, HttpMethod method, long delayMs, boolean generateId) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                generateId, null);
        return createCreateController(config, ControllerSaveInfoMode.COLLECTION_ONE);
    }

    public UpdateController createStaticUpdateController(String uri, HttpMethod method, String answer, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, answer, delayMs,
                false, null);
        return createUpdateController(config, ControllerSaveInfoMode.STATIC);
    }

    public UpdateController createCollectionOneUpdateController(String uri, HttpMethod method, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                false, null);
        return createUpdateController(config, ControllerSaveInfoMode.COLLECTION_ONE);
    }

    public RouterController createRouterController(String fromUri, String toUri, HttpMethod method) {
        RouterConfig routerConfig = createRouterConfig(fromUri, toUri, method);
        return new RouterController(routerConfig, restClient);
    }

    public GroovyController createGroovyController(String uri, HttpMethod method, long delayMs, String groovyScript) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                false, groovyScript);
        return new GroovyController(config, controllerData);
    }

    private ControllerConfig createControllerConfig(String uri, HttpMethod method, ControllerFunctionMode functionMode,
                                                    String answer, long delayMs, boolean generateId, String groovyScript) {
        ControllerConfig config = new ControllerConfig();
        config.setUri(uri);
        config.setMethod(method);
        config.setFunctionMode(functionMode);
        config.setAnswer(answer);
        config.setDelayMs(delayMs);
        config.setGenerateId(generateId);
        config.setGroovyScript(groovyScript);
        List<String> idParams = HttpUtils.getIdParams(uri);
        config.setIdParams(idParams);
        idParams.forEach(id -> config.getGenerateIdPatterns().put(id, GeneratorPattern.SEQUENCE));
        return config;
    }

    private RouterConfig createRouterConfig(String uri, String toUrl, HttpMethod method) {
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
                .build();
    }

    private DeleteController createDeleteController(ControllerConfig config, ControllerSaveInfoMode saveInfoMode) {
        return DeleteController.builder()
                .saveInfoMode(saveInfoMode)
                .controllerData(controllerData)
                .controllerConfig(config)
                .build();
    }

    private CreateController createCreateController(ControllerConfig config, ControllerSaveInfoMode saveInfoMode) {
        return CreateController.builder()
                .saveInfoMode(saveInfoMode)
                .controllerData(controllerData)
                .controllerConfig(config)
                .idGenerator(new IdGenerator())
                .build();
    }

    private UpdateController createUpdateController(ControllerConfig config, ControllerSaveInfoMode saveInfoMode) {
        return UpdateController.builder()
                .saveInfoMode(saveInfoMode)
                .controllerData(controllerData)
                .controllerConfig(config)
                .build();
    }

}
