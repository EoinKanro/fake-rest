package io.github.eoinkanro.fakerest.core.controller;

import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import io.undertow.server.HttpServerExchange;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeleteControllerTest extends FakeModifyControllerTest<DeleteController> {

    @Override
    DeleteController initStaticController_NullRequest_InternalServerError(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, EMPTY_REQUEST_BODY, delayMs);
    }

    @Override
    DeleteController initStaticController_StaticAnswer(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, REQUEST_BODY, delayMs);
    }

    @Override
    DeleteController initStaticController_BodyAnswer(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    DeleteController initStaticController_EmptyRequestBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    DeleteController initStaticController_NullRequestBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    void collectionOneControllerOneId_NotFound(HttpMethod requestMethod, long delayMs) {
        fillData();
        DeleteController subj = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServerExchange request = createRequestWithUriVariables(requestMethod, null, createRequestUri(FIRST_ID_PARAM, BAD_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST_BAD, Collections.singletonList(FIRST_ID_PARAM));
        assertEquals(createKeyNotFoundError(key).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NotFound(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NotFound(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NotFound(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NotFound(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_Found(HttpMethod requestMethod, long delayMs) {
        fillData();
        DeleteController subj = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServerExchange request = createRequestWithUriVariables(requestMethod, null, createRequestUri(FIRST_ID_PARAM, FIRST_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(JSON_ONE_ID_FIRST.toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST_BAD, Collections.singletonList(FIRST_ID_PARAM));
        assertFalse(controllerData.containsKey(TEST_COLLECTION_URI_ONE_ID, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_Found(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_Found(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_Found(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_Found(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NotFound(HttpMethod requestMethod, long delayMs) {
        fillData();
        DeleteController subj = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        HttpServerExchange request = createRequestWithUriVariables(requestMethod, null,
                createRequestUri(FIRST_ID_PARAM, BAD_ID_VALUE,
                        SECOND_ID_PARAM, BAD_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_TWO_ID_BAD, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertEquals(createKeyNotFoundError(key).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NotFound(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NotFound(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NotFound(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NotFound(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_Found(HttpMethod requestMethod, long delayMs) {
        fillData();
        DeleteController subj = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        HttpServerExchange request = createRequestWithUriVariables(requestMethod, null,
                createRequestUri(FIRST_ID_PARAM, FIRST_ID_VALUE,
                        SECOND_ID_PARAM, SECOND_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(JSON_TWO_ID.toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertFalse(controllerData.containsKey(TEST_COLLECTION_URI_TWO_IDS, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_Found(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_Found(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_Found(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_Found(requestMethod, delayMs);
    }
}
