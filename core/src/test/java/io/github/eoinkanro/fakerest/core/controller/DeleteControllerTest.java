package io.github.eoinkanro.fakerest.core.controller;

import io.github.eoinkanro.fakerest.core.FakeRestApplication;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FakeRestApplication.class)
class DeleteControllerTest extends FakeModifyControllerTest<DeleteController> {

    @Override
    DeleteController initStaticController_NullRequest_InternalServerError(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, EMPTY_REQUEST_BODY, delayMs);
    }

    @Override
    DeleteController initStaticController_StaticAnswer(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, REQUEST_BODY, delayMs);
    }

    @Override
    DeleteController initStaticController_BodyAnswer(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    DeleteController initStaticController_EmptyRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    DeleteController initStaticController_NullRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    void collectionOneControllerOneId_NotFound(RequestMethod requestMethod, long delayMs) {
        fillData();
        DeleteController subj = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequestWithUriVariables(requestMethod, null, Collections.singletonMap(FIRST_ID_PARAM, BAD_ID_VALUE));
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST_BAD, Collections.singletonList(FIRST_ID_PARAM));
        assertEquals(createKeyNotFoundError(key).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
        DeleteController subj = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequestWithUriVariables(requestMethod, null, Collections.singletonMap(FIRST_ID_PARAM, FIRST_ID_VALUE));
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(JSON_ONE_ID_FIRST.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST_BAD, Collections.singletonList(FIRST_ID_PARAM));
        assertFalse(controllerData.containsKey(TEST_COLLECTION_URI_ONE_ID, key));
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
        DeleteController subj = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, BAD_ID_VALUE);
        variables.put(SECOND_ID_PARAM, BAD_ID_VALUE);
        HttpServletRequest request = createRequestWithUriVariables(requestMethod, null, variables);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_TWO_ID_BAD, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertEquals(createKeyNotFoundError(key).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
        DeleteController subj = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, FIRST_ID_VALUE);
        variables.put(SECOND_ID_PARAM, SECOND_ID_VALUE);
        HttpServletRequest request = createRequestWithUriVariables(requestMethod, null, variables);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(JSON_TWO_ID.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertFalse(controllerData.containsKey(TEST_COLLECTION_URI_TWO_IDS, key));
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
