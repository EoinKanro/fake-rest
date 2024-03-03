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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
class UpdateControllerTest extends FakeModifyControllerTest<UpdateController> {

    @Override
    UpdateController initStaticController_NullRequest_InternalServerError(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, EMPTY_REQUEST_BODY, delayMs);
    }

    @Override
    UpdateController initStaticController_StaticAnswer(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, REQUEST_BODY, delayMs);
    }

    @Override
    UpdateController initStaticController_BodyAnswer(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    UpdateController initStaticController_EmptyRequestBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    UpdateController initStaticController_NullRequestBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    void collectionOneController_NullBody_BadRequest(HttpMethod requestMethod, long delayMs) {
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
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
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
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
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
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
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
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

    void collectionOneControllerOneId_UrlIdNotFound_BadRequest(HttpMethod requestMethod, long delayMs) {
        clearData();
        initData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);

        HttpServerExchange request = createRequestWithUriVariables(requestMethod, JSON_ONE_ID_FIRST.toString(), createRequestUri(FIRST_ID_PARAM, FIRST_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));

        assertEquals(createBadRequest(String.format(FakeModifyController.KEY_NOT_FOUND, key)).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_UrlIdNotFound_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_UrlIdNotFound_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_UrlIdNotFound_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_UrlIdNotFound_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
        fillData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);

        String key = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));
        ObjectNode json = JSON_ONE_ID_FIRST.deepCopy();
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_ONE_ID, key));

        JsonUtils.putString(json, DATA_PARAM, getRandomString());
        assertNotEquals(json, controllerData.getData(TEST_COLLECTION_URI_ONE_ID, key));

        HttpServerExchange request = createRequestWithUriVariables(requestMethod, json.toString(), createRequestUri(FIRST_ID_PARAM, FIRST_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);

        assertEquals(json.toString(), response.getBody());
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_ONE_ID, key));
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_UrlId_Updated(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_UrlId_Updated(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_JsonNoId_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
        fillData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);

        String key = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));
        ObjectNode json = JSON_NO_ID.deepCopy();
        assertNotEquals(json, controllerData.getData(TEST_COLLECTION_URI_ONE_ID, key));

        HttpServerExchange request = createRequestWithUriVariables(requestMethod, json.toString(), createRequestUri(FIRST_ID_PARAM, FIRST_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        ObjectNode responseJson = JsonUtils.toObjectNode(response.getBody());

        assertNotEquals(json, responseJson);
        JsonUtils.putString(json, FIRST_ID_PARAM, FIRST_ID_VALUE);
        assertEquals(json, responseJson);
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_ONE_ID, key));
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_JsonNoId_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_JsonNoId_UrlId_Updated(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_JsonNoId_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_JsonNoId_UrlId_Updated(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_UrlIdNotFound_BadRequest(HttpMethod requestMethod, long delayMs) {
        clearData();
        initData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);

        HttpServerExchange request = createRequestWithUriVariables(requestMethod, JSON_TWO_ID.toString(),
                createRequestUri(FIRST_ID_PARAM, FIRST_ID_VALUE,
                        SECOND_ID_PARAM, SECOND_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));

        assertEquals(createBadRequest(String.format(FakeModifyController.KEY_NOT_FOUND, key)).toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_UrlIdNotFound_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_UrlIdNotFound_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_UrlIdNotFound_BadRequest(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_UrlIdNotFound_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
        fillData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);

        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        ObjectNode json = JSON_TWO_ID.deepCopy();
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_TWO_IDS, key));

        JsonUtils.putString(json, DATA_PARAM, getRandomString());
        assertNotEquals(json, controllerData.getData(TEST_COLLECTION_URI_TWO_IDS, key));

        HttpServerExchange request = createRequestWithUriVariables(requestMethod, json.toString(),
                createRequestUri(FIRST_ID_PARAM, FIRST_ID_VALUE,
                        SECOND_ID_PARAM, SECOND_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);

        assertEquals(json.toString(), response.getBody());
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_TWO_IDS, key));
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_UrlId_Updated(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_UrlId_Updated(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_JsonNoId_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
        fillData();
        UpdateController subj = testControllersFabric.createCollectionOneUpdateController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);

        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        ObjectNode json = JSON_NO_ID.deepCopy();
        assertNotEquals(json, controllerData.getData(TEST_COLLECTION_URI_TWO_IDS, key));

        HttpServerExchange request = createRequestWithUriVariables(requestMethod, json.toString(),
                createRequestUri(FIRST_ID_PARAM, FIRST_ID_VALUE,
                        SECOND_ID_PARAM, SECOND_ID_VALUE));
        ControllerResponse response = handleResponse(subj, request, delayMs);
        ObjectNode responseJson = JsonUtils.toObjectNode(response.getBody());

        assertNotEquals(json, responseJson);
        JsonUtils.putString(json, FIRST_ID_PARAM, FIRST_ID_VALUE);
        JsonUtils.putString(json, SECOND_ID_PARAM, SECOND_ID_VALUE);
        assertEquals(json, responseJson);
        assertEquals(json, controllerData.getData(TEST_COLLECTION_URI_TWO_IDS, key));
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_JsonNoId_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_JsonNoId_UrlId_Updated(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_JsonNoId_UrlId_Updated(HttpMethod requestMethod, long delayMs) {
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
