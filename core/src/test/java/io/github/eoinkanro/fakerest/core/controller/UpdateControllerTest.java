package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.fakerest.core.FakeRestApplication;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(classes = FakeRestApplication.class)
class UpdateControllerTest extends FakeModifyControllerTest<UpdateController> {

    @Override
    UpdateController initStaticController_NullRequest_InternalServerError(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, EMPTY_REQUEST_BODY, delayMs);
    }

    @Override
    UpdateController initStaticController_StaticAnswer(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, REQUEST_BODY, delayMs);
    }

    @Override
    UpdateController initStaticController_BodyAnswer(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    UpdateController initStaticController_EmptyRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    UpdateController initStaticController_NullRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    void collectionOneController_NullBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequest(requestMethod, null);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.NULL_BODY).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneController_NoDelay_NullBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneController_NullBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneController_WithDelay_NullBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneController_NullBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneController_EmptyBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.NULL_BODY).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneController_NoDelay_EmptyBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneController_EmptyBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneController_WithDelay_EmptyBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneController_EmptyBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneController_NotJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequest(requestMethod, REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(String.format(FakeModifyController.DATA_NOT_JSON, REQUEST_BODY)).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneController_NoDelay_NotJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneController_NotJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneController_WithDelay_NotJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneController_NotJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneController_EmptyJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_JSON_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(String.format(FakeModifyController.DATA_NOT_JSON, EMPTY_JSON_BODY)).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneController_NoDelay_EmptyJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneController_EmptyJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneController_WithDelay_EmptyJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneController_EmptyJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_UrlIdNotFound_BadRequest(RequestMethod requestMethod, long delayMs) {
        clearData();
        initData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, FIRST_ID_VALUE);

        HttpServletRequest request = createRequestWithUriVariables(requestMethod, JSON_ONE_ID_FIRST.toString(), variables);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));

        assertEquals(createBadRequest(String.format(FakeModifyController.KEY_NOT_FOUND, key)).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_UrlIdNotFound_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_UrlIdNotFound_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_UrlIdNotFound_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_UrlIdNotFound_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        fillData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, FIRST_ID_VALUE);

        String key = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));
        ObjectNode json = JSON_ONE_ID_FIRST.deepCopy();
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_ONE_ID, key));

        jsonUtils.putString(json, DATA_PARAM, getRandomString());
        assertNotEquals(json, controllerData.getData(TEST_COLLECTION_URI_ONE_ID, key));

        HttpServletRequest request = createRequestWithUriVariables(requestMethod, json.toString(), variables);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);

        assertEquals(json.toString(), response.getBody());
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_ONE_ID, key));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_UrlId_Updated(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_UrlId_Updated(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_JsonNoId_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        fillData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, FIRST_ID_VALUE);

        String key = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));
        ObjectNode json = JSON_NO_ID.deepCopy();
        assertNotEquals(json, controllerData.getData(TEST_COLLECTION_URI_ONE_ID, key));

        HttpServletRequest request = createRequestWithUriVariables(requestMethod, json.toString(), variables);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        ObjectNode responseJson = jsonUtils.toObjectNode(response.getBody());

        assertNotEquals(json, responseJson);
        jsonUtils.putString(json, FIRST_ID_PARAM, FIRST_ID_VALUE);
        assertEquals(json, responseJson);
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_ONE_ID, key));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_JsonNoId_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_JsonNoId_UrlId_Updated(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_JsonNoId_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_JsonNoId_UrlId_Updated(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_UrlIdNotFound_BadRequest(RequestMethod requestMethod, long delayMs) {
        clearData();
        initData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, FIRST_ID_VALUE);
        variables.put(SECOND_ID_PARAM, SECOND_ID_VALUE);

        HttpServletRequest request = createRequestWithUriVariables(requestMethod, JSON_TWO_ID.toString(), variables);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));

        assertEquals(createBadRequest(String.format(FakeModifyController.KEY_NOT_FOUND, key)).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_UrlIdNotFound_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_UrlIdNotFound_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_UrlIdNotFound_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_UrlIdNotFound_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        fillData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, FIRST_ID_VALUE);
        variables.put(SECOND_ID_PARAM, SECOND_ID_VALUE);

        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        ObjectNode json = JSON_TWO_ID.deepCopy();
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_TWO_IDS, key));

        jsonUtils.putString(json, DATA_PARAM, getRandomString());
        assertNotEquals(json, controllerData.getData(TEST_COLLECTION_URI_TWO_IDS, key));

        HttpServletRequest request = createRequestWithUriVariables(requestMethod, json.toString(), variables);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);

        assertEquals(json.toString(), response.getBody());
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_TWO_IDS, key));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_UrlId_Updated(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_UrlId_Updated(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_JsonNoId_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        fillData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, FIRST_ID_VALUE);
        variables.put(SECOND_ID_PARAM, SECOND_ID_VALUE);

        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        ObjectNode json = JSON_NO_ID.deepCopy();
        assertNotEquals(json, controllerData.getData(TEST_COLLECTION_URI_TWO_IDS, key));

        HttpServletRequest request = createRequestWithUriVariables(requestMethod, json.toString(), variables);
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        ObjectNode responseJson = jsonUtils.toObjectNode(response.getBody());

        assertNotEquals(json, responseJson);
        jsonUtils.putString(json, FIRST_ID_PARAM, FIRST_ID_VALUE);
        jsonUtils.putString(json, SECOND_ID_PARAM, SECOND_ID_VALUE);
        assertEquals(json, responseJson);
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_TWO_IDS, key));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_JsonNoId_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_JsonNoId_UrlId_Updated(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_JsonNoId_UrlId_Updated(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_JsonNoId_UrlId_Updated(requestMethod, delayMs);
    }

    private String getRandomString() {
        char[] dictionary = new char[] {'A', 'B', 'C', 'D'};
        char[] array = new char[10];
        Random random = new Random();
        for (int i = 0 ; i < array.length; i++) {
            int dictionaryIndex = random.nextInt(dictionary.length);
            array[i] = dictionary[dictionaryIndex];
        }
        return new String(array);
    }
}
