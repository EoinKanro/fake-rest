package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CreateControllerTest extends FakeModifyControllerTest<CreateController> {

    @Override
    CreateController initStaticController_NullRequest_InternalServerError(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticCreateController(TEST_STATIC_URI, requestMethod, EMPTY_REQUEST_BODY, delayMs);
    }

    @Override
    CreateController initStaticController_StaticAnswer(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticCreateController(TEST_STATIC_URI, requestMethod, REQUEST_BODY, delayMs);
    }

    @Override
    CreateController initStaticController_BodyAnswer(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticCreateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    CreateController initStaticController_EmptyRequestBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticCreateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    CreateController initStaticController_NullRequestBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticCreateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    void collectionOneController_NullBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, null);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.NULL_BODY).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneController_NoDelay_NullBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneController_NullBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneController_WithDelay_NullBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneController_NullBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneController_EmptyBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.NULL_BODY).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneController_NoDelay_EmptyBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneController_EmptyBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneController_WithDelay_EmptyBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneController_EmptyBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneController_NotJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, REQUEST_BODY);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(String.format(FakeModifyController.DATA_NOT_JSON, REQUEST_BODY)).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneController_NoDelay_NotJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneController_NotJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneController_WithDelay_NotJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneController_NotJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneController_EmptyJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, EMPTY_JSON_BODY);
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(String.format(FakeModifyController.DATA_NOT_JSON, EMPTY_JSON_BODY)).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneController_NoDelay_EmptyJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneController_EmptyJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneController_WithDelay_EmptyJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneController_EmptyJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_NoGenerateId_NoIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, JSON_NO_ID.toString());
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.MISSING_IDS).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NoGenerateId_NoIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_NoIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NoGenerateId_NoIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_NoIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_NoGenerateId_EmptyIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, JSON_ONE_ID_EMPTY_ID.toString());
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.MISSING_IDS).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NoGenerateId_EmptyIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_EmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NoGenerateId_EmptyIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_EmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_AlreadyExist(HttpMethod requestMethod, long delayMs) {
        fillData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, JSON_ONE_ID_FIRST.toString());
        ControllerResponse response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_ONE_ID, key));
        assertEquals(createBadRequest(String.format(FakeModifyController.KEY_ALREADY_EXIST, key)).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NoGenerateId_WithIdJsonBody_AlreadyExist(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_AlreadyExist(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NoGenerateId_WithIdJsonBody_AlreadyExist(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_AlreadyExist(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        clearData();
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, JSON_ONE_ID_FIRST.toString());
        ControllerResponse response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));
        assertEquals(JSON_ONE_ID_FIRST.toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_ONE_ID, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NoGenerateId_WithIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_SaveData(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NoGenerateId_WithIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NoGenerateId_WithIdJsonBody_SaveData(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_GenerateId_NoIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        clearData();
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs, true);
        HttpServerExchange request = createRequest(requestMethod, JSON_NO_ID.toString());
        ControllerResponse response = handleResponse(subj, request, delayMs);
        ObjectNode responseBody = JsonUtils.toObjectNode(response.getBody());
        JsonUtils.putString(JSON_NO_ID, FIRST_ID_PARAM, JsonUtils.getString(responseBody, FIRST_ID_PARAM));
        String key = controllerData.buildKey(JSON_NO_ID, Collections.singletonList(FIRST_ID_PARAM));
        assertEquals(JSON_NO_ID, responseBody);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_ONE_ID, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId__NoDelay_GenerateId_NoIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_GenerateId_NoIdJsonBody_SaveData(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_GenerateId_NoIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_GenerateId_NoIdJsonBody_SaveData(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NoGenerateId_NoIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, JSON_NO_ID.toString());
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.MISSING_IDS).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NoGenerateId_NoIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_NoIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NoGenerateId_NoIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_NoIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NoGenerateId_TwoEmptyIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, JSON_TWO_ID_EMPTY_ID.toString());
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.MISSING_IDS).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NoGenerateId_TwoEmptyIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_TwoEmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NoGenerateId_TwoEmptyIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_TwoEmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NoGenerateId_OneEmptyIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, JSON_ONE_ID_EMPTY_ID.toString());
        ControllerResponse response = handleResponse(subj, request, delayMs);
        assertEquals(createBadRequest(FakeModifyController.MISSING_IDS).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NoGenerateId_OneEmptyIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_OneEmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NoGenerateId_OneEmptyIdJsonBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_OneEmptyIdJsonBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_AlreadyExist(HttpMethod requestMethod, long delayMs) {
        fillData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, JSON_TWO_ID.toString());
        ControllerResponse response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_TWO_IDS, key));
        assertEquals(createBadRequest(String.format(FakeModifyController.KEY_ALREADY_EXIST, key)).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NoGenerateId_WithIdJsonBody_AlreadyExist(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_AlreadyExist(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NoGenerateId_WithIdJsonBody_AlreadyExist(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_AlreadyExist(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        clearData();
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, false);
        HttpServerExchange request = createRequest(requestMethod, JSON_TWO_ID.toString());
        ControllerResponse response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertEquals(JSON_TWO_ID.toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_TWO_IDS, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NoGenerateId_WithIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_SaveData(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NoGenerateId_WithIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NoGenerateId_WithIdJsonBody_SaveData(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_GenerateId_NoIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        clearData();
        initData();
        CreateController subj = testControllersFabric.createCollectionOneCreateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs, true);
        HttpServerExchange request = createRequest(requestMethod, JSON_NO_ID.toString());
        ControllerResponse response = handleResponse(subj, request, delayMs);
        ObjectNode responseBody = JsonUtils.toObjectNode(response.getBody());
        JsonUtils.putString(JSON_NO_ID, FIRST_ID_PARAM, JsonUtils.getString(responseBody, FIRST_ID_PARAM));
        JsonUtils.putString(JSON_NO_ID, SECOND_ID_PARAM, JsonUtils.getString(responseBody, SECOND_ID_PARAM));
        String key = controllerData.buildKey(JSON_NO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertEquals(JSON_NO_ID, responseBody);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(controllerData.containsKey(TEST_COLLECTION_URI_TWO_IDS, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_GenerateId_NoIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_GenerateId_NoIdJsonBody_SaveData(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_GenerateId_NoIdJsonBody_SaveData(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_GenerateId_NoIdJsonBody_SaveData(requestMethod, delayMs);
    }
}
