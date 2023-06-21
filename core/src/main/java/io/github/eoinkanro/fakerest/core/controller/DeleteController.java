package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Controller that can delete data from collection
 */
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteController extends FakeModifyController {

    @Override
    protected ResponseEntity<String> handleOne(HttpServletRequest request, String body) {
        ResponseEntity<String> result;

        String key = controllerData.buildKey(httpUtils.getUrlIds(request), controllerConfig.getIdParams());
        if (controllerData.containsKey(controllerConfig.getUri(), key)) {
            ObjectNode data = controllerData.getData(controllerConfig.getUri(), key);
            controllerData.deleteData(controllerConfig.getUri(), key);

            result = new ResponseEntity<>(data.toString(), HttpStatus.OK);
        } else {
            ObjectNode error = JsonUtils.createJson();
            JsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_NOT_FOUND, key));
            result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
        }

        return result;
    }
}
