package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.FakeRestApplication;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FakeRestApplication.class)
class GroovyControllerTest extends FakeControllerTest {

    void sendRequest_UseUri_ReturnUri(RequestMethod method, long delay) {
        String groovyScript = "return new GroovyAnswer(HttpStatus.OK, uri);";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        HttpServletRequest request = createRequest(method, null);
        ResponseEntity<String> response = handleResponse(subj, request, delay);
        assertEquals(TEST_STATIC_URI, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_UseUri_ReturnUri(RequestMethod method, long delay) {
        sendRequest_UseUri_ReturnUri(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelay_UseUri_ReturnUri(RequestMethod method, long delay) {
        sendRequest_UseUri_ReturnUri(method, delay);
    }

    void sendRequest_UseJsonUtilsAndControllerData_ReturnJson(RequestMethod method, long delay) {
        String groovyScript = "import java.util.Collections;" +
                              "ObjectNode json = jsonUtils.createJson();" +
                              "jsonUtils.putString(json, \"id\", \"id-value\");" +
                              "String key = controllerData.buildKey(json, Collections.singletonList(\"id\"));" +
                              "controllerData.putData(uri, key, json);" +
                              "return new GroovyAnswer(HttpStatus.OK, json.toString());";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        ObjectNode expectedJson = jsonUtils.createJson();
        jsonUtils.putString(expectedJson, "id", "id-value");

        HttpServletRequest request = createRequest(method, null);
        ResponseEntity<String> response = handleResponse(subj, request, delay);

        assertEquals(expectedJson.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ObjectNode dataFromCollection = controllerData.getData(TEST_STATIC_URI, controllerData.buildKey(expectedJson, Collections.singletonList(FIRST_ID_PARAM)));
        assertEquals(expectedJson, dataFromCollection);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_UseJsonUtilsAndControllerData_ReturnJson(RequestMethod method, long delay) {
        sendRequest_UseJsonUtilsAndControllerData_ReturnJson(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelay_UseJsonUtilsAndControllerData_ReturnJson(RequestMethod method, long delay) {
        sendRequest_UseJsonUtilsAndControllerData_ReturnJson(method, delay);
    }

    void sendRequest_UseBody_ReturnBody(RequestMethod method, long delay) {
        String groovyScript = "return new GroovyAnswer(HttpStatus.OK, body)";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        HttpServletRequest request = createRequest(method, REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(subj, request, delay);
        assertEquals(REQUEST_BODY, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_UseBody_ReturnBody(RequestMethod method, long delay) {
        sendRequest_UseBody_ReturnBody(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelayUseBody_ReturnBody(RequestMethod method, long delay) {
        sendRequest_UseBody_ReturnBody(method, delay);
    }

    void sendRequest_UseHeaders_ReturnHeader(RequestMethod method, long delay) {
        String groovyScript = "return new GroovyAnswer(HttpStatus.OK, headers.get(\"id\").toString())";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        Map<String, String> header = new HashMap<>();
        header.put(FIRST_ID_PARAM, FIRST_ID_VALUE);
        HttpServletRequest request = createRequestWithHeaders(method, null, header);
        ResponseEntity<String> response = handleResponse(subj, request, delay);
        List<String> expectedAnswer = Collections.singletonList(FIRST_ID_VALUE);
        assertEquals(expectedAnswer.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_UseHeaders_ReturnHeader(RequestMethod method, long delay) {
        sendRequest_UseHeaders_ReturnHeader(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelay_UseHeaders_ReturnHeader(RequestMethod method, long delay) {
        sendRequest_UseHeaders_ReturnHeader(method, delay);
    }

    void sendRequest_BadScript_ReturnError(RequestMethod method, long delay) {
        String groovyScript = "badCode()";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        HttpServletRequest request = createRequest(method, null);
        ResponseEntity<String> response = handleResponse(subj, request, delay);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains(FakeController.DESCRIPTION_PARAM));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_BadScript_ReturnError(RequestMethod method, long delay) {
        sendRequest_BadScript_ReturnError(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelay_BadScript_ReturnError(RequestMethod method, long delay) {
        sendRequest_BadScript_ReturnError(method, delay);
    }

    void sendRequest_NoStatus_ReturnOk(RequestMethod method, long delay) {
        String groovyScript = "return new GroovyAnswer(null, null);";
        GroovyController subj = testControllersFabric.createGroovyController(TEST_STATIC_URI, method, delay, groovyScript);
        HttpServletRequest request = createRequest(method, null);
        ResponseEntity<String> response = handleResponse(subj, request, delay);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void sendRequest_NoDelay_NoStatus_ReturnOk(RequestMethod method, long delay) {
        sendRequest_NoStatus_ReturnOk(method, delay);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void sendRequest_WithDelay_NoStatus_ReturnOk(RequestMethod method, long delay) {
        sendRequest_NoStatus_ReturnOk(method, delay);
    }
}
