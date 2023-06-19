package io.github.eoinkanro.fakerest.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

abstract class FakeModifyControllerTest<T extends FakeModifyController> extends FakeControllerTest {

    void staticController_NullRequest_InternalServerError(FakeModifyController subj, long delayMs) {
        ResponseEntity<String> response = handleResponse(subj, null, delayMs);
        assertNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_NullRequest_InternalServerError(RequestMethod requestMethod, long delayMs) {
        T controller = initStaticController_NullRequest_InternalServerError(requestMethod, delayMs);
        staticController_NullRequest_InternalServerError(controller, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_NullRequest_InternalServerError(RequestMethod requestMethod, long delayMs) {
        T controller = initStaticController_NullRequest_InternalServerError(requestMethod, delayMs);
        staticController_NullRequest_InternalServerError(controller, delayMs);
    }

    void staticController_StaticAnswer(FakeModifyController subj, RequestMethod requestMethod, long delayMs) {
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(REQUEST_BODY, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_StaticAnswer(RequestMethod requestMethod, long delayMs) {
        T controller = initStaticController_StaticAnswer(requestMethod, delayMs);
        staticController_StaticAnswer(controller, requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_StaticAnswer(RequestMethod requestMethod, long delayMs) {
        T controller = initStaticController_StaticAnswer(requestMethod, delayMs);
        staticController_StaticAnswer(controller, requestMethod, delayMs);
    }

    void staticController_BodyAnswer(FakeModifyController subj, RequestMethod requestMethod, long delayMs) {
        HttpServletRequest request = createRequest(requestMethod, REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(REQUEST_BODY, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_BodyAnswer(RequestMethod requestMethod, long delayMs) {
        T controller = initStaticController_BodyAnswer(requestMethod, delayMs);
        staticController_BodyAnswer(controller, requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_BodyAnswer(RequestMethod requestMethod, long delayMs) {
        T controller = initStaticController_BodyAnswer(requestMethod, delayMs);
        staticController_BodyAnswer(controller, requestMethod, delayMs);
    }

    void staticController_EmptyRequestBody_BadRequest(FakeModifyController subj, RequestMethod requestMethod, long delayMs) {
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.NULL_BODY_OR_ANSWER).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_EmptyRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        T controller = initStaticController_EmptyRequestBody_BadRequest(requestMethod, delayMs);
        staticController_EmptyRequestBody_BadRequest(controller, requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_EmptyRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        T controller = initStaticController_EmptyRequestBody_BadRequest(requestMethod, delayMs);
        staticController_EmptyRequestBody_BadRequest(controller, requestMethod, delayMs);
    }

    void staticController_NullRequestBody_BadRequest(FakeModifyController subj, RequestMethod requestMethod, long delayMs) {
        HttpServletRequest request = createRequest(requestMethod, null);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.NULL_BODY_OR_ANSWER).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_NullRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        T controller = initStaticController_NullRequestBody_BadRequest(requestMethod, delayMs);
        staticController_NullRequestBody_BadRequest(controller, requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_NullRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        T controller = initStaticController_NullRequestBody_BadRequest(requestMethod, delayMs);
        staticController_NullRequestBody_BadRequest(controller, requestMethod, delayMs);
    }

    abstract T initStaticController_NullRequest_InternalServerError(RequestMethod requestMethod, long delayMs);
    abstract T initStaticController_StaticAnswer(RequestMethod requestMethod, long delayMs);
    abstract T initStaticController_BodyAnswer(RequestMethod requestMethod, long delayMs);
    abstract T initStaticController_EmptyRequestBody_BadRequest(RequestMethod requestMethod, long delayMs);
    abstract T initStaticController_NullRequestBody_BadRequest(RequestMethod requestMethod, long delayMs);
}
