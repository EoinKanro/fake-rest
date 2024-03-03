package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.github.eoinkanro.fakerest.core.model.enums.GeneratorPattern;
import io.github.eoinkanro.fakerest.core.utils.IdGenerator;
import io.undertow.server.HttpServerExchange;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Controller that can save data to collection
 */
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateController extends FakeModifyController {

    private IdGenerator idGenerator;

    @Override
    protected ControllerResponse handleOne(HttpServerExchange request, String body) {
        ControllerResponse result;
        if (body != null && !body.isEmpty()) {
            result = saveOne(body);
        } else {
            ObjectNode error = JsonUtils.createJson();
            JsonUtils.putString(error, DESCRIPTION_PARAM, NULL_BODY);
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(error.toString())
                    .build();
        }
        return result;
    }

    /**
     * Save info to collection
     *
     * @param body - request body
     * @return - response
     */
    private ControllerResponse saveOne(String body) {
        ControllerResponse result = null;
        ObjectNode bodyJson = JsonUtils.toObjectNode(body);

        if (bodyJson != null && !bodyJson.isEmpty()) {
            if (controllerConfig.isGenerateId()) {
                addId(bodyJson);
            } else if (!checkIds(bodyJson)){
                ObjectNode error = JsonUtils.createJson();
                JsonUtils.putString(error, DESCRIPTION_PARAM, MISSING_IDS);
                result = ControllerResponse.builder()
                        .status(HttpServletResponse.SC_BAD_REQUEST)
                        .body(error.toString())
                        .build();
            }

            if (result == null) {
                result = saveOne(bodyJson);
            }
        } else {
            ObjectNode error = JsonUtils.createJson();
            JsonUtils.putString(error, DESCRIPTION_PARAM, String.format(DATA_NOT_JSON, body));
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(error.toString())
                    .build();
        }
        return result;
    }

    /**
     * Save info to collection
     *
     * @param body - request body
     * @return - response
     */
    private ControllerResponse saveOne(ObjectNode body) {
        ControllerResponse result;
        String key = controllerData.buildKey(body, controllerConfig.getIdParams());

        if (!controllerData.containsKey(controllerConfig.getUri(), key)) {
            controllerData.putData(controllerConfig.getUri(), key, body);
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_OK)
                    .body(body.toString())
                    .build();
        } else {
            ObjectNode error = JsonUtils.createJson();
            JsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_ALREADY_EXIST, key));
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(error.toString())
                    .build();
        }
        return result;
    }

    /**
     * Check all ids exist and not empty
     *
     * @param data - data from request
     * @return - all ids exist and not empty
     */
    private boolean checkIds(ObjectNode data) {
        boolean result = true;
        for (String id : controllerConfig.getIdParams()) {
            String idValue = JsonUtils.getString(data, id);
            if (idValue == null || idValue.isEmpty()) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Add id to data
     *
     * @param data - data from request
     */
    private void addId(ObjectNode data) {
        Map<String, GeneratorPattern> generatorPatterns = controllerConfig.getGenerateIdPatterns();
        controllerConfig.getIdParams().forEach(idParam -> {
            GeneratorPattern pattern = generatorPatterns == null ? null : generatorPatterns.get(idParam);
            JsonUtils.putString(data, idParam, idGenerator.generateId(pattern));
        });
    }

}
