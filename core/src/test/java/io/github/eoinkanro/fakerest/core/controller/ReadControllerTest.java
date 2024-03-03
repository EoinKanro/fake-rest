package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import io.undertow.server.HttpServerExchange;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ReadControllerTest extends FakeControllerTest {

    void staticController_EmptyAnswer(HttpMethod requestMethod, long delayMs) {
        ReadController subj = testControllersFabric.createStaticReadController(TEST_STATIC_URI, requestMethod, EMPTY_REQUEST_BODY, delayMs);
        HttpServerExchange request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(EMPTY_REQUEST_BODY, response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_EmptyAnswer(HttpMethod requestMethod, long delayMs) {
        staticController_EmptyAnswer(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_EmptyAnswer(HttpMethod requestMethod, long delayMs) {
        staticController_EmptyAnswer(requestMethod, delayMs);
    }

    void staticController_NotEmptyAnswer(HttpMethod requestMethod, long delayMs) {
        ReadController subj = testControllersFabric.createStaticReadController(TEST_STATIC_URI, requestMethod, REQUEST_BODY, delayMs);
        HttpServerExchange request = createRequest(requestMethod, REQUEST_BODY);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(REQUEST_BODY, response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_NotEmptyAnswer(HttpMethod requestMethod, long delayMs) {
        staticController_NotEmptyAnswer(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_NotEmptyAnswer(HttpMethod requestMethod, long delayMs) {
        staticController_NotEmptyAnswer(requestMethod, delayMs);
    }

    void collectionAllControllerOneId_EmptyArray(HttpMethod requestMethod, long delayMs) {
        clearData();
        ReadController subj = testControllersFabric.createCollectionAllReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServerExchange request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(JsonUtils.createArray().toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionAllControllerOneId_NoDelay_EmptyArray(HttpMethod requestMethod, long delayMs) {
        collectionAllControllerOneId_EmptyArray(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionAllControllerOneId_WithDelay_EmptyArray(HttpMethod requestMethod, long delayMs) {
        collectionAllControllerOneId_EmptyArray(requestMethod, delayMs);
    }

    void collectionAllControllerOneId_NotEmptyArray(HttpMethod requestMethod, long delayMs) {
        fillData();
        ReadController subj = testControllersFabric.createCollectionAllReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServerExchange request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        ArrayNode expectedArray = JsonUtils.createArray();
        expectedArray.add(JSON_ONE_ID_FIRST);
        expectedArray.add(JSON_ONE_ID_SECOND);
        assertEquals(expectedArray.toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionAllControllerOneId_NoDelay_NotEmptyArray(HttpMethod requestMethod, long delayMs) {
        collectionAllControllerOneId_NotEmptyArray(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionAllControllerOneId_WithDelay_NotEmptyArray(HttpMethod requestMethod, long delayMs) {
        collectionAllControllerOneId_NotEmptyArray(requestMethod, delayMs);
    }

    void collectionAllControllerTwoId_NotEmptyArray(HttpMethod requestMethod, long delayMs) {
        fillData();
        ReadController subj = testControllersFabric.createCollectionAllReadController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        HttpServerExchange request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        ArrayNode expectedArray = JsonUtils.createArray();
        expectedArray.add(JSON_TWO_ID);
        assertEquals(expectedArray.toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionAllControllerTwoId_NoDelay_NotEmptyArray(HttpMethod requestMethod, long delayMs) {
        collectionAllControllerTwoId_NotEmptyArray(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionAllControllerTwoId_WithDelay_NotEmptyArray(HttpMethod requestMethod, long delayMs) {
        collectionAllControllerTwoId_NotEmptyArray(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_NotFound(HttpMethod requestMethod, long delayMs) {
        fillData();
        ReadController subj = testControllersFabric.createCollectionOneReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServerExchange request = createRequestWithUriVariables(requestMethod, EMPTY_REQUEST_BODY, createRequestUri(FIRST_ID_PARAM, BAD_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST_BAD, Collections.singletonList(FIRST_ID_PARAM));
        assertEquals(createKeyNotFoundError(key).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
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
        ReadController subj = testControllersFabric.createCollectionOneReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServerExchange request = createRequestWithUriVariables(requestMethod, EMPTY_REQUEST_BODY, createRequestUri(FIRST_ID_PARAM, FIRST_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(JSON_ONE_ID_FIRST.toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
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
        ReadController subj = testControllersFabric.createCollectionOneReadController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        HttpServerExchange request = createRequestWithUriVariables(requestMethod, EMPTY_REQUEST_BODY,
                createRequestUri(FIRST_ID_PARAM, BAD_ID_VALUE,
                        SECOND_ID_PARAM, BAD_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_TWO_ID_BAD, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertEquals(createKeyNotFoundError(key).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
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
        ReadController subj = testControllersFabric.createCollectionOneReadController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        HttpServerExchange request = createRequestWithUriVariables(requestMethod, EMPTY_REQUEST_BODY,
                createRequestUri(FIRST_ID_PARAM, FIRST_ID_VALUE,
                        SECOND_ID_PARAM, SECOND_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(JSON_TWO_ID.toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
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
