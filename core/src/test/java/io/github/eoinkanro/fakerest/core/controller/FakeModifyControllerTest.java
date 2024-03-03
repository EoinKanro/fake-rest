package io.github.eoinkanro.fakerest.core.controller;

import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import io.undertow.server.HttpServerExchange;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

abstract class FakeModifyControllerTest<T extends FakeModifyController> extends FakeControllerTest {

    void staticController_NullRequest_InternalServerError(FakeModifyController subj, long delayMs) {
        ControllerResponse response = handleResponse(subj, null, delayMs);
        assertNull(response.getBody());
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_NullRequest_InternalServerError(HttpMethod requestMethod, long delayMs) {
        T controller = initStaticController_NullRequest_InternalServerError(requestMethod, delayMs);
        staticController_NullRequest_InternalServerError(controller, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_NullRequest_InternalServerError(HttpMethod requestMethod, long delayMs) {
        T controller = initStaticController_NullRequest_InternalServerError(requestMethod, delayMs);
        staticController_NullRequest_InternalServerError(controller, delayMs);
    }

    void staticController_StaticAnswer(FakeModifyController subj, HttpMethod requestMethod, long delayMs) {
        HttpServerExchange request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(REQUEST_BODY, response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_StaticAnswer(HttpMethod requestMethod, long delayMs) {
        T controller = initStaticController_StaticAnswer(requestMethod, delayMs);
        staticController_StaticAnswer(controller, requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_StaticAnswer(HttpMethod requestMethod, long delayMs) {
        T controller = initStaticController_StaticAnswer(requestMethod, delayMs);
        staticController_StaticAnswer(controller, requestMethod, delayMs);
    }

    void staticController_BodyAnswer(FakeModifyController subj, HttpMethod requestMethod, long delayMs) {
        HttpServerExchange request = createRequest(requestMethod, REQUEST_BODY);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(REQUEST_BODY, response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_BodyAnswer(HttpMethod requestMethod, long delayMs) {
        T controller = initStaticController_BodyAnswer(requestMethod, delayMs);
        staticController_BodyAnswer(controller, requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_BodyAnswer(HttpMethod requestMethod, long delayMs) {
        T controller = initStaticController_BodyAnswer(requestMethod, delayMs);
        staticController_BodyAnswer(controller, requestMethod, delayMs);
    }

    void staticController_EmptyRequestBody_BadRequest(FakeModifyController subj, HttpMethod requestMethod, long delayMs) {
        HttpServerExchange request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.NULL_BODY_OR_ANSWER).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_EmptyRequestBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        T controller = initStaticController_EmptyRequestBody_BadRequest(requestMethod, delayMs);
        staticController_EmptyRequestBody_BadRequest(controller, requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_EmptyRequestBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        T controller = initStaticController_EmptyRequestBody_BadRequest(requestMethod, delayMs);
        staticController_EmptyRequestBody_BadRequest(controller, requestMethod, delayMs);
    }

    void staticController_NullRequestBody_BadRequest(FakeModifyController subj, HttpMethod requestMethod, long delayMs) {
        HttpServerExchange request = createRequest(requestMethod, null);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.NULL_BODY_OR_ANSWER).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_NullRequestBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        T controller = initStaticController_NullRequestBody_BadRequest(requestMethod, delayMs);
        staticController_NullRequestBody_BadRequest(controller, requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_NullRequestBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        T controller = initStaticController_NullRequestBody_BadRequest(requestMethod, delayMs);
        staticController_NullRequestBody_BadRequest(controller, requestMethod, delayMs);
    }

    abstract T initStaticController_NullRequest_InternalServerError(HttpMethod requestMethod, long delayMs);
    abstract T initStaticController_StaticAnswer(HttpMethod requestMethod, long delayMs);
    abstract T initStaticController_BodyAnswer(HttpMethod requestMethod, long delayMs);
    abstract T initStaticController_EmptyRequestBody_BadRequest(HttpMethod requestMethod, long delayMs);
    abstract T initStaticController_NullRequestBody_BadRequest(HttpMethod requestMethod, long delayMs);
}
