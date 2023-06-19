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

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FakeRestApplication.class)
class CreateControllerTest extends FakeModifyControllerTest<CreateController> {

    @Override
    CreateController initStaticController_NullRequest_InternalServerError(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticCreateController(TEST_STATIC_URI, requestMethod, EMPTY_REQUEST_BODY, delayMs);
    }

    @Override
    CreateController initStaticController_StaticAnswer(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticCreateController(TEST_STATIC_URI, requestMethod, REQUEST_BODY, delayMs);
    }

    @Override
    CreateController initStaticController_BodyAnswer(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticCreateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    CreateController initStaticController_EmptyRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticCreateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    CreateController initStaticController_NullRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticCreateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    void collectionOneController_NullBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
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
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
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
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
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
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
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

    void collectionOneControllerOneId_NoGenerateId_NoIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServletRequest request = createRequest(requestMethod, JSON_NO_ID.toString());
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.MISSING_IDS).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NoGenerateId_NoIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_NoIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NoGenerateId_NoIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_NoIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_NoGenerateId_EmptyIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServletRequest request = createRequest(requestMethod, JSON_ONE_ID_EMPTY_ID.toString());
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.MISSING_IDS).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NoGenerateId_EmptyIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_EmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NoGenerateId_EmptyIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_EmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_AlreadyExist(RequestMethod requestMethod, long delayMs) {
        fillData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServletRequest request = createRequest(requestMethod, JSON_ONE_ID_FIRST.toString());
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_ONE_ID, key));
        assertEquals(createBadRequest(String.format(FakeModifyController.KEY_ALREADY_EXIST, key)).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NoGenerateId_WithIdJsonBody_AlreadyExist(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_AlreadyExist(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NoGenerateId_WithIdJsonBody_AlreadyExist(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_AlreadyExist(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        clearData();
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServletRequest request = createRequest(requestMethod, JSON_ONE_ID_FIRST.toString());
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));
        assertEquals(JSON_ONE_ID_FIRST.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_ONE_ID, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NoGenerateId_WithIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_SaveData(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NoGenerateId_WithIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_SaveData(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_GenerateId_NoIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        clearData();
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, true);
        HttpServletRequest request = createRequest(requestMethod, JSON_NO_ID.toString());
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        ObjectNode responseBody = jsonUtils.toObjectNode(response.getBody());
        jsonUtils.putString(JSON_NO_ID, FIRST_ID_PARAM, jsonUtils.getString(responseBody, FIRST_ID_PARAM));
        String key = controllerData.buildKey(JSON_NO_ID, Collections.singletonList(FIRST_ID_PARAM));
        assertEquals(JSON_NO_ID, responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_ONE_ID, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId__NoDelay_GenerateId_NoIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_GenerateId_NoIdJsonBody_SaveData(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_GenerateId_NoIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_GenerateId_NoIdJsonBody_SaveData(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NoGenerateId_NoIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, false);
        HttpServletRequest request = createRequest(requestMethod, JSON_NO_ID.toString());
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.MISSING_IDS).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NoGenerateId_NoIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_NoIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NoGenerateId_NoIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_NoIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NoGenerateId_TwoEmptyIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, false);
        HttpServletRequest request = createRequest(requestMethod, JSON_TWO_ID_EMPTY_ID.toString());
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.MISSING_IDS).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NoGenerateId_TwoEmptyIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_TwoEmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NoGenerateId_TwoEmptyIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_TwoEmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NoGenerateId_OneEmptyIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, false);
        HttpServletRequest request = createRequest(requestMethod, JSON_ONE_ID_EMPTY_ID.toString());
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.MISSING_IDS).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NoGenerateId_OneEmptyIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_OneEmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NoGenerateId_OneEmptyIdJsonBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_OneEmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_AlreadyExist(RequestMethod requestMethod, long delayMs) {
        fillData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, false);
        HttpServletRequest request = createRequest(requestMethod, JSON_TWO_ID.toString());
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_TWO_IDS, key));
        assertEquals(createBadRequest(String.format(FakeModifyController.KEY_ALREADY_EXIST, key)).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NoGenerateId_WithIdJsonBody_AlreadyExist(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_AlreadyExist(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NoGenerateId_WithIdJsonBody_AlreadyExist(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_AlreadyExist(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        clearData();
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, false);
        HttpServletRequest request = createRequest(requestMethod, JSON_TWO_ID.toString());
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertEquals(JSON_TWO_ID.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_TWO_IDS, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NoGenerateId_WithIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_SaveData(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NoGenerateId_WithIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_SaveData(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_GenerateId_NoIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        clearData();
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, true);
        HttpServletRequest request = createRequest(requestMethod, JSON_NO_ID.toString());
        ResponseEntity<String> response = handleResponse(subj, request, delayMs);
        ObjectNode responseBody = jsonUtils.toObjectNode(response.getBody());
        jsonUtils.putString(JSON_NO_ID, FIRST_ID_PARAM, jsonUtils.getString(responseBody, FIRST_ID_PARAM));
        jsonUtils.putString(JSON_NO_ID, SECOND_ID_PARAM, jsonUtils.getString(responseBody, SECOND_ID_PARAM));
        String key = controllerData.buildKey(JSON_NO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertEquals(JSON_NO_ID, responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_TWO_IDS, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_GenerateId_NoIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_GenerateId_NoIdJsonBody_SaveData(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_GenerateId_NoIdJsonBody_SaveData(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_GenerateId_NoIdJsonBody_SaveData(requestMethod, delayMs);
    }
}
