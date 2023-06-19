package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.fakerest.core.model.ControllerSaveInfoMode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Base class for CUD controllers that can modify data in collection
 */
@Slf4j
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class FakeModifyController extends FakeController {

    private static final String LOG_INFO = "Got request \r\nMethod: [{}] \r\nUri: [{}] \r\nBody: [{}]";

    protected static final String NULL_BODY = "body is null";
    protected static final String NULL_BODY_OR_ANSWER = "body is null and answer not specified";
    protected static final String MISSING_IDS = "some ids are missing";

    @Override
    public final ResponseEntity<String> handle(HttpServletRequest request) {
        delay();

        ResponseEntity<String> result = null;
        String body = null;
        try {
            body = httpUtils.readBody(request);
        } catch (Exception e) {
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (log.isTraceEnabled()) log.trace(LOG_INFO, request.getMethod(), request.getRequestURI(), body);

        if (result == null) {
            result = processRequest(request, body);
        }
        return result;
    }

    /**
     * Process handled request
     *
     * @param request - request to controller
     * @param body - body from request
     * @return - response
     */
    private ResponseEntity<String> processRequest(HttpServletRequest request, String body) {
        ResponseEntity<String> result;
        if (saveInfoMode == ControllerSaveInfoMode.COLLECTION_ONE) {
            result = handleOne(request, body);
        } else {
            result = returnAnswerOrBody(body);
        }
        return result;
    }

    /**
     * Return static data
     *
     * @param body - body from request
     * @return - response
     */
    protected ResponseEntity<String> returnAnswerOrBody(String body) {
        ResponseEntity<String> result;
        if (controllerConfig.getAnswer() != null) {
            result = new ResponseEntity<>(controllerConfig.getAnswer(), HttpStatus.OK);
        }else if (body != null && !body.isEmpty()) {
            result = new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            ObjectNode badRequest = jsonUtils.createJson();
            jsonUtils.putString(badRequest, DESCRIPTION_PARAM, NULL_BODY_OR_ANSWER);
            result = new ResponseEntity<>(badRequest.toString(), HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    /**
     * Process request in controller with mode {@link ControllerSaveInfoMode#COLLECTION_ONE}
     *
     * @param request - request to controller
     * @param body - body from request
     * @return - response
     */
    protected abstract ResponseEntity<String> handleOne(HttpServletRequest request, String body);
}
