package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Controller that can update data in collection
 */
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateController extends FakeModifyController {

    @Override
    protected ResponseEntity<String> handleOne(HttpServletRequest request, String body) {
        ResponseEntity<String> result;
        if (body != null && !body.isEmpty()) {
            result = updateOne(request, body);
        } else {
            ObjectNode error = JsonUtils.createJson();
            JsonUtils.putString(error, DESCRIPTION_PARAM, NULL_BODY);
            result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
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
    private ResponseEntity<String> updateOne(HttpServletRequest request, String body) {
        ResponseEntity<String> result;
        ObjectNode bodyJson = JsonUtils.toObjectNode(body);

        if (bodyJson != null && !bodyJson.isEmpty()) {
            Map<String, String> ids = httpUtils.getUrlIds(request);
            String key = controllerData.buildKey(ids, controllerConfig.getIdParams());

            if (controllerData.containsKey(controllerConfig.getUri(), key)) {
                ids.forEach((id, value) -> JsonUtils.putString(bodyJson, id, value));

                controllerData.putData(controllerConfig.getUri(), key, bodyJson);
                result = new ResponseEntity<>(bodyJson.toString(), HttpStatus.OK);
            } else {
                ObjectNode error = JsonUtils.createJson();
                JsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_NOT_FOUND, key));
                result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
            }
        } else {
            ObjectNode error = JsonUtils.createJson();
            JsonUtils.putString(error, DESCRIPTION_PARAM, String.format(DATA_NOT_JSON, body));
            result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
        }
        return result;
    }
}
