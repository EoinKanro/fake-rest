package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.FakeRestApplication;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FareRestTestApplication.class)
class ReadControllerTest extends FakeControllerTest {

    void staticController_EmptyAnswer(RequestMethod requestMethod, long delayMs) {
        ReadController subj = testControllersFabric.createStaticReadController(TEST_STATIC_URI, requestMethod, EMPTY_REQUEST_BODY, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(EMPTY_REQUEST_BODY, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_EmptyAnswer(RequestMethod requestMethod, long delayMs) {
        staticController_EmptyAnswer(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_EmptyAnswer(RequestMethod requestMethod, long delayMs) {
        staticController_EmptyAnswer(requestMethod, delayMs);
    }

    void staticController_NotEmptyAnswer(RequestMethod requestMethod, long delayMs) {
        ReadController subj = testControllersFabric.createStaticReadController(TEST_STATIC_URI, requestMethod, REQUEST_BODY, delayMs);
        HttpServletRequest request = createRequest(requestMethod, REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(REQUEST_BODY, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_NotEmptyAnswer(RequestMethod requestMethod, long delayMs) {
        staticController_NotEmptyAnswer(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_NotEmptyAnswer(RequestMethod requestMethod, long delayMs) {
        staticController_NotEmptyAnswer(requestMethod, delayMs);
    }

    void collectionAllControllerOneId_EmptyArray(RequestMethod requestMethod, long delayMs) {
        clearData();
        ReadController subj = testControllersFabric.createCollectionAllReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(JsonUtils.createArray().toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionAllControllerOneId_NoDelay_EmptyArray(RequestMethod requestMethod, long delayMs) {
        collectionAllControllerOneId_EmptyArray(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionAllControllerOneId_WithDelay_EmptyArray(RequestMethod requestMethod, long delayMs) {
        collectionAllControllerOneId_EmptyArray(requestMethod, delayMs);
    }

    void collectionAllControllerOneId_NotEmptyArray(RequestMethod requestMethod, long delayMs) {
        fillData();
        ReadController subj = testControllersFabric.createCollectionAllReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        ArrayNode expectedArray = JsonUtils.createArray();
        expectedArray.add(JSON_ONE_ID_FIRST);
        expectedArray.add(JSON_ONE_ID_SECOND);
        assertEquals(expectedArray.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionAllControllerOneId_NoDelay_NotEmptyArray(RequestMethod requestMethod, long delayMs) {
        collectionAllControllerOneId_NotEmptyArray(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionAllControllerOneId_WithDelay_NotEmptyArray(RequestMethod requestMethod, long delayMs) {
        collectionAllControllerOneId_NotEmptyArray(requestMethod, delayMs);
    }

    void collectionAllControllerTwoId_NotEmptyArray(RequestMethod requestMethod, long delayMs) {
        fillData();
        ReadController subj = testControllersFabric.createCollectionAllReadController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        ArrayNode expectedArray = JsonUtils.createArray();
        expectedArray.add(JSON_TWO_ID);
        assertEquals(expectedArray.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionAllControllerTwoId_NoDelay_NotEmptyArray(RequestMethod requestMethod, long delayMs) {
        collectionAllControllerTwoId_NotEmptyArray(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionAllControllerTwoId_WithDelay_NotEmptyArray(RequestMethod requestMethod, long delayMs) {
        collectionAllControllerTwoId_NotEmptyArray(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_NotFound(RequestMethod requestMethod, long delayMs) {
        fillData();
        ReadController subj = testControllersFabric.createCollectionOneReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequestWithUriVariables(requestMethod, EMPTY_REQUEST_BODY, Collections.singletonMap(FIRST_ID_PARAM, BAD_ID_VALUE));
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST_BAD, Collections.singletonList(FIRST_ID_PARAM));
        assertEquals(createKeyNotFoundError(key).toString(), response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NotFound(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NotFound(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NotFound(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NotFound(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_Found(RequestMethod requestMethod, long delayMs) {
        fillData();
        ReadController subj = testControllersFabric.createCollectionOneReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequestWithUriVariables(requestMethod, EMPTY_REQUEST_BODY, Collections.singletonMap(FIRST_ID_PARAM, FIRST_ID_VALUE));
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(JSON_ONE_ID_FIRST.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_Found(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_Found(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_Found(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_Found(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NotFound(RequestMethod requestMethod, long delayMs) {
        fillData();
        ReadController subj = testControllersFabric.createCollectionOneReadController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, BAD_ID_VALUE);
        variables.put(SECOND_ID_PARAM, BAD_ID_VALUE);
        HttpServletRequest request = createRequestWithUriVariables(requestMethod, EMPTY_REQUEST_BODY, variables);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_TWO_ID_BAD, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertEquals(createKeyNotFoundError(key).toString(), response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NotFound(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NotFound(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NotFound(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NotFound(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_Found(RequestMethod requestMethod, long delayMs) {
        fillData();
        ReadController subj = testControllersFabric.createCollectionOneReadController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, FIRST_ID_VALUE);
        variables.put(SECOND_ID_PARAM, SECOND_ID_VALUE);
        HttpServletRequest request = createRequestWithUriVariables(requestMethod, EMPTY_REQUEST_BODY, variables);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(JSON_TWO_ID.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_Found(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_Found(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_Found(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_Found(requestMethod, delayMs);
    }
}
