package io.github.eoinkanro.fakerest.core.controller;

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
    public final ControllerResponse handle(HttpServerExchange request) {
        delay();

        ControllerResponse result = null;
        String body = null;
        try {
            body = HttpUtils.readBody(request);
            log.trace(LOG_INFO, request.getRequestMethod(), request.getRequestURI(), body);
        } catch (Exception e) {
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .build();
        }

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
    private ControllerResponse processRequest(HttpServerExchange request, String body) {
        ControllerResponse result;
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
    protected ControllerResponse returnAnswerOrBody(String body) {
        ControllerResponse result;
        if (controllerConfig.getAnswer() != null && !controllerConfig.getAnswer().isBlank()) {
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_OK)
                    .body(controllerConfig.getAnswer())
                    .build();
        }else if (body != null && !body.isEmpty()) {
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_OK)
                    .body(body)
                    .build();
        } else {
            ObjectNode badRequest = JsonUtils.createJson();
            JsonUtils.putString(badRequest, DESCRIPTION_PARAM, NULL_BODY_OR_ANSWER);
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(badRequest.toString())
                    .build();
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
    protected abstract ControllerResponse handleOne(HttpServerExchange request, String body);

}
