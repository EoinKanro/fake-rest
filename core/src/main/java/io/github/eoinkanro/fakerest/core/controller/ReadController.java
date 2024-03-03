package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.github.eoinkanro.fakerest.core.model.enums.ControllerSaveInfoMode;
import io.github.eoinkanro.fakerest.core.utils.HttpUtils;
import io.undertow.server.HttpServerExchange;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Controller that can read data from collection
 */
@Slf4j
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadController extends FakeController {

    private static final String LOG_INFO = "Got request \r\nMethod: [{}] \r\nUri: [{}]";

    @Override
    public ControllerResponse handle(HttpServerExchange request) {
        log.trace(LOG_INFO, request.getRequestMethod(), request.getRequestURI());
        delay();

        ControllerResponse result;
        if (saveInfoMode == ControllerSaveInfoMode.COLLECTION_ALL) {
            result = handleAll();
        } else if (saveInfoMode == ControllerSaveInfoMode.COLLECTION_ONE) {
            result = handleId(request);
        } else {
            result = handleNoId();
        }
        return result;
    }

    /**
     * Process request to get all data from collection
     *
     * @return - response
     */
    private ControllerResponse handleAll() {
        ControllerResponse result;
        Map<String, ObjectNode> allData = controllerData.getAllData(controllerConfig.getUri());

        ArrayNode array = JsonUtils.createArray();
        allData.forEach((key, data) -> array.add(data));
        result = ControllerResponse.builder()
                .status(HttpServletResponse.SC_OK)
                .body(array.toString())
                .build();

        return result;
    }

    /**
     * Process request to get data by id
     *
     * @param request - request to controller
     * @return - response
     */
    private ControllerResponse handleId(HttpServerExchange request) {
        ControllerResponse result;

        Map<String, String> urlIds = HttpUtils.getUrlIds(request, controllerConfig.getIdParams());
        String key = controllerData.buildKey(urlIds, controllerConfig.getIdParams());

        if (controllerData.containsKey(controllerConfig.getUri(), key)) {
            ObjectNode data = controllerData.getData(controllerConfig.getUri(), key);
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_OK)
                    .body(data.toString())
                    .build();
        } else {
            ObjectNode error = JsonUtils.createJson();
            JsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_NOT_FOUND, key));
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_NOT_FOUND)
                    .body(error.toString())
                    .build();
        }
        return result;
    }

    /**
     * Return static answer
     *
     * @return - response
     */
    private ControllerResponse handleNoId() {
        return ControllerResponse.builder()
                .status(HttpServletResponse.SC_OK)
                .body(controllerConfig.getAnswer() == null ? "" : controllerConfig.getAnswer())
                .build();
    }

}
