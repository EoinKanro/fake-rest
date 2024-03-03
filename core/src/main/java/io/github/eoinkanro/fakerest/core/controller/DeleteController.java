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

/**
 * Controller that can delete data from collection
 */
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteController extends FakeModifyController {

    @Override
    protected ControllerResponse handleOne(HttpServerExchange request, String body) {
        ControllerResponse result;

        String key = controllerData.buildKey(HttpUtils.getUrlIds(request, controllerConfig.getIdParams()), controllerConfig.getIdParams());
        if (controllerData.containsKey(controllerConfig.getUri(), key)) {
            ObjectNode data = controllerData.getData(controllerConfig.getUri(), key);
            controllerData.deleteData(controllerConfig.getUri(), key);

            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_OK)
                    .body(data.toString())
                    .build();
        } else {
            ObjectNode error = JsonUtils.createJson();
            JsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_NOT_FOUND, key));
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(error.toString())
                    .build();
        }

        return result;
    }
}
