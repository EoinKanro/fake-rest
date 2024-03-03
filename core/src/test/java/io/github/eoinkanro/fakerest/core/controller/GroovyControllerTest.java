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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class GroovyControllerTest extends FakeControllerTest {

    void sendRequest_UseUri_ReturnUri(HttpMethod method, long delay) {
        String groovyScript = "return new ControllerResponse(HttpServletResponse.SC_OK, uri);";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        HttpServerExchange request = createRequest(method, null);
        ControllerResponse response = handleResponse(subj, request, delay);
        assertEquals(TEST_STATIC_URI, response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_UseUri_ReturnUri(HttpMethod method, long delay) {
        sendRequest_UseUri_ReturnUri(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelay_UseUri_ReturnUri(HttpMethod method, long delay) {
        sendRequest_UseUri_ReturnUri(method, delay);
    }

    void sendRequest_UseJsonUtilsAndControllerData_ReturnJson(HttpMethod method, long delay) {
        String groovyScript = "import java.util.Collections;" +
                              "ObjectNode json = JsonUtils.createJson();" +
                              "JsonUtils.putString(json, \"id\", \"id-value\");" +
                              "String key = controllerData.buildKey(json, Collections.singletonList(\"id\"));" +
                              "controllerData.putData(uri, key, json);" +
                              "return new ControllerResponse(HttpServletResponse.SC_OK, json.toString());";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        ObjectNode expectedJson = JsonUtils.createJson();
        JsonUtils.putString(expectedJson, "id", "id-value");

        HttpServerExchange request = createRequest(method, null);
        ControllerResponse response = handleResponse(subj, request, delay);

        assertEquals(expectedJson.toString(), response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        ObjectNode dataFromCollection = controllerData.getData(TEST_STATIC_URI, controllerData.buildKey(expectedJson, Collections.singletonList(FIRST_ID_PARAM)));
        assertEquals(expectedJson, dataFromCollection);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_UseJsonUtilsAndControllerData_ReturnJson(HttpMethod method, long delay) {
        sendRequest_UseJsonUtilsAndControllerData_ReturnJson(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelay_UseJsonUtilsAndControllerData_ReturnJson(HttpMethod method, long delay) {
        sendRequest_UseJsonUtilsAndControllerData_ReturnJson(method, delay);
    }

    void sendRequest_UseBody_ReturnBody(HttpMethod method, long delay) {
        String groovyScript = "return new ControllerResponse(HttpServletResponse.SC_OK, body)";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        HttpServerExchange request = createRequest(method, REQUEST_BODY);
        ControllerResponse response = handleResponse(subj, request, delay);
        assertEquals(REQUEST_BODY, response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_UseBody_ReturnBody(HttpMethod method, long delay) {
        sendRequest_UseBody_ReturnBody(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelayUseBody_ReturnBody(HttpMethod method, long delay) {
        sendRequest_UseBody_ReturnBody(method, delay);
    }

    void sendRequest_UseHeaders_ReturnHeader(HttpMethod method, long delay) {
        String groovyScript = "return new ControllerResponse(HttpServletResponse.SC_OK, headers.firstValue(\"id\").get())";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        Map<String, String> header = new HashMap<>();
        header.put(FIRST_ID_PARAM, FIRST_ID_VALUE);
        HttpServerExchange request = createRequestWithHeaders(method, null, header);
        ControllerResponse response = handleResponse(subj, request, delay);
        assertEquals(FIRST_ID_VALUE, response.getBody());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_UseHeaders_ReturnHeader(HttpMethod method, long delay) {
        sendRequest_UseHeaders_ReturnHeader(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelay_UseHeaders_ReturnHeader(HttpMethod method, long delay) {
        sendRequest_UseHeaders_ReturnHeader(method, delay);
    }

    void sendRequest_BadScript_ReturnError(HttpMethod method, long delay) {
        String groovyScript = "badCode()";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        HttpServerExchange request = createRequest(method, null);
        ControllerResponse response = handleResponse(subj, request, delay);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertTrue(response.getBody().contains(FakeController.DESCRIPTION_PARAM));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_BadScript_ReturnError(HttpMethod method, long delay) {
        sendRequest_BadScript_ReturnError(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelay_BadScript_ReturnError(HttpMethod method, long delay) {
        sendRequest_BadScript_ReturnError(method, delay);
    }

    void sendRequest_NoStatus_ReturnOk(HttpMethod method, long delay) {
        String groovyScript = "return new ControllerResponse(0, null);";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        HttpServerExchange request = createRequest(method, null);
        ControllerResponse response = handleResponse(subj, request, delay);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_NoStatus_ReturnOk(HttpMethod method, long delay) {
        sendRequest_NoStatus_ReturnOk(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelay_NoStatus_ReturnOk(HttpMethod method, long delay) {
        sendRequest_NoStatus_ReturnOk(method, delay);
    }
}
