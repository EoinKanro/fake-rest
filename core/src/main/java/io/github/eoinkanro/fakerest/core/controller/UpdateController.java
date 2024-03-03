package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.github.eoinkanro.fakerest.core.utils.HttpUtils;
import io.undertow.server.HttpServerExchange;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Controller that can update data in collection
 */
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateController extends FakeModifyController {

    @Override
    protected ControllerResponse handleOne(HttpServerExchange request, String body) {
        ControllerResponse result;
        if (body != null && !body.isEmpty()) {
            result = updateOne(request, body);
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
     * Update data in collection
     *
     * @param request - request
     * @param body - body from request
     * @return - response
     */
    private ControllerResponse updateOne(HttpServerExchange request, String body) {
        ControllerResponse result;
        ObjectNode bodyJson = JsonUtils.toObjectNode(body);

        if (bodyJson != null && !bodyJson.isEmpty()) {
            Map<String, String> ids = HttpUtils.getUrlIds(request, controllerConfig.getIdParams());
            String key = controllerData.buildKey(ids, controllerConfig.getIdParams());

            if (controllerData.containsKey(controllerConfig.getUri(), key)) {
                ids.forEach((id, value) -> JsonUtils.putString(bodyJson, id, value));

                controllerData.putData(controllerConfig.getUri(), key, bodyJson);
                result = ControllerResponse.builder()
                        .status(HttpServletResponse.SC_OK)
                        .body(bodyJson.toString())
                        .build();
            } else {
                ObjectNode error = JsonUtils.createJson();
                JsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_NOT_FOUND, key));
                result = ControllerResponse.builder()
                        .status(HttpServletResponse.SC_BAD_REQUEST)
                        .body(error.toString())
                        .build();
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
}
