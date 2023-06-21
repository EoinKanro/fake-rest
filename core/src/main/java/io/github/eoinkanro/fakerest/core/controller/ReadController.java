package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.model.ControllerSaveInfoMode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public ResponseEntity<String> handle(HttpServletRequest request) {
        if (log.isTraceEnabled()) log.trace(LOG_INFO, request.getMethod(), request.getRequestURI());
        delay();

        ResponseEntity<String> result;
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
    private ResponseEntity<String> handleAll() {
        ResponseEntity<String> result;
        Map<String, ObjectNode> allData = controllerData.getAllData(controllerConfig.getUri());
        if (allData.size() > 0) {
            ArrayNode array = JsonUtils.createArray();
            allData.forEach((key, data) -> array.add(data));
            result = new ResponseEntity<>(array.toString(), HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(JsonUtils.createArray().toString(), HttpStatus.OK);
        }
        return result;
    }

    /**
     * Process request to get data by id
     *
     * @param request - request to controller
     * @return - response
     */
    private ResponseEntity<String> handleId(HttpServletRequest request) {
        ResponseEntity<String> result;

        Map<String, String> urlIds = httpUtils.getUrlIds(request);
        String key = controllerData.buildKey(urlIds, controllerConfig.getIdParams());

        if (controllerData.containsKey(controllerConfig.getUri(), key)) {
            ObjectNode data = controllerData.getData(controllerConfig.getUri(), key);
            result = new ResponseEntity<>(data.toString(), HttpStatus.OK);
        } else {
            ObjectNode error = JsonUtils.createJson();
            JsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_NOT_FOUND, key));
            result = new ResponseEntity<>(error.toString(), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    /**
     * Return static answer
     *
     * @return - response
     */
    private ResponseEntity<String> handleNoId() {
        return new ResponseEntity<>(controllerConfig.getAnswer(), HttpStatus.OK);
    }

}
