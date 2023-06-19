package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.fakerest.core.model.GeneratorPattern;
import io.github.eoinkanro.fakerest.core.utils.IdGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Controller that can save data to collection
 */
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateController extends FakeModifyController {

    private IdGenerator idGenerator;

    @Override
    protected ResponseEntity<String> handleOne(HttpServletRequest request, String body) {
        ResponseEntity<String> result;
        if (body != null && !body.isEmpty()) {
            result = saveOne(body);
        } else {
            ObjectNode error = jsonUtils.createJson();
            jsonUtils.putString(error, DESCRIPTION_PARAM, NULL_BODY);
            result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    /**
     * Save info to collection
     *
     * @param body - request body
     * @return - response
     */
    private ResponseEntity<String> saveOne(String body) {
        ResponseEntity<String> result = null;
        ObjectNode bodyJson = jsonUtils.toObjectNode(body);

        if (bodyJson != null && !bodyJson.isEmpty()) {
            if (controllerConfig.isGenerateId()) {
                addId(bodyJson);
            } else if (!checkIds(bodyJson)){
                ObjectNode error = jsonUtils.createJson();
                jsonUtils.putString(error, DESCRIPTION_PARAM, MISSING_IDS);
                result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
            }

            if (result == null) {
                result = saveOne(bodyJson);
            }
        } else {
            ObjectNode error = jsonUtils.createJson();
            jsonUtils.putString(error, DESCRIPTION_PARAM, String.format(DATA_NOT_JSON, body));
            result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    /**
     * Save info to collection
     *
     * @param body - request body
     * @return - response
     */
    private ResponseEntity<String> saveOne(ObjectNode body) {
        ResponseEntity<String> result;
        String key = controllerData.buildKey(body, controllerConfig.getIdParams());

        if (!controllerData.containsKey(controllerConfig.getUri(), key)) {
            controllerData.putData(controllerConfig.getUri(), key, body);
            result = new ResponseEntity<>(body.toString(), HttpStatus.OK);
        } else {
            ObjectNode error = jsonUtils.createJson();
            jsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_ALREADY_EXIST, key));
            result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
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
            String idValue = jsonUtils.getString(data, id);
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
            jsonUtils.putString(data, idParam, idGenerator.generateId(pattern));
        });
    }

}
