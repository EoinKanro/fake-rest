package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.conf.MappingConfigurationLoader;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import io.github.ivanrosw.fakerest.core.utils.SystemUtils;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

abstract class FakeControllerTest {
    private static final long DELAY_MS = 10;
    static final String TEST_STATIC_URI = "/test";
    static final String TEST_COLLECTION_URI_ONE_ID = "/test/{id}";
    static final String TEST_COLLECTION_URI_TWO_IDS = "/test/{id}/{id2}";
    static final String REQUEST_BODY = "body";
    static final String EMPTY_REQUEST_BODY = "";

    static final String FIRST_ID_PARAM="id";
    static final String FIRST_ID_VALUE ="id-value";
    static final String SECOND_ID_PARAM="id2";
    static final String SECOND_ID_VALUE="id2-value";
    static final String BAD_ID_VALUE="bad-value";
    static final String DATA_PARAM="data";
    static final String FIRST_DATA_VALUE ="data-value";
    static final String SECOND_DATA_VALUE ="data-value2";

    @Autowired
    TestControllersFabric testControllersFabric;
    @Autowired
    ControllerData controllerData;
    @Autowired
    JsonUtils jsonUtils;
    @MockBean
    MappingConfigurationLoader mappingConfigurationLoader;
    @SpyBean
    SystemUtils systemUtils;

    ObjectNode JSON_ONE_ID_FIRST;
    ObjectNode JSON_ONE_ID_FIRST_BAD;
    ObjectNode JSON_ONE_ID_SECOND;
    ObjectNode JSON_TWO_ID;
    ObjectNode JSON_TWO_ID_BAD;


    private static Stream<Arguments> provideAllMethodsDelay(long delayMs) {
        RequestMethod[] requestMethods = RequestMethod.values();
        Arguments[] arguments = new Arguments[requestMethods.length];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = Arguments.of(requestMethods[i], delayMs);
        }
        return Stream.of(arguments);
    }

    /**
     * Arguments for tests
     */
    static Stream<Arguments> provideAllMethodsNoDelay() {
        return provideAllMethodsDelay(0);
    }

    /**
     * Arguments for tests
     */
    static Stream<Arguments> provideAllMethodsWithDelay() {
        return provideAllMethodsDelay(DELAY_MS);
    }

    /**
     * Fill controller data
     */
    void fillData() {
        clearData();
        JSON_ONE_ID_FIRST = jsonUtils.createJson();
        jsonUtils.putString(JSON_ONE_ID_FIRST, FIRST_ID_PARAM, FIRST_ID_VALUE);
        jsonUtils.putString(JSON_ONE_ID_FIRST, DATA_PARAM, FIRST_DATA_VALUE);
        String firstKey = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));
        controllerData.putData(TEST_COLLECTION_URI_ONE_ID, firstKey, JSON_ONE_ID_FIRST);

        JSON_ONE_ID_SECOND = jsonUtils.createJson();
        jsonUtils.putString(JSON_ONE_ID_SECOND, FIRST_ID_PARAM, SECOND_ID_VALUE);
        jsonUtils.putString(JSON_ONE_ID_SECOND, DATA_PARAM, SECOND_DATA_VALUE);
        String secondKey = controllerData.buildKey(JSON_ONE_ID_SECOND, Collections.singletonList(FIRST_ID_PARAM));
        controllerData.putData(TEST_COLLECTION_URI_ONE_ID, secondKey, JSON_ONE_ID_SECOND);

        JSON_TWO_ID = jsonUtils.createJson();
        jsonUtils.putString(JSON_TWO_ID, FIRST_ID_PARAM, FIRST_ID_VALUE);
        jsonUtils.putString(JSON_TWO_ID, SECOND_ID_PARAM, SECOND_ID_VALUE);
        jsonUtils.putString(JSON_TWO_ID, DATA_PARAM, FIRST_DATA_VALUE);
        String keyTwo = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        controllerData.putData(TEST_COLLECTION_URI_TWO_IDS, keyTwo, JSON_TWO_ID);

        JSON_ONE_ID_FIRST_BAD = jsonUtils.createJson();
        jsonUtils.putString(JSON_ONE_ID_FIRST_BAD, FIRST_ID_PARAM, BAD_ID_VALUE);

        JSON_TWO_ID_BAD = jsonUtils.createJson();
        jsonUtils.putString(JSON_TWO_ID_BAD, FIRST_ID_PARAM, BAD_ID_VALUE);
        jsonUtils.putString(JSON_TWO_ID_BAD, SECOND_ID_PARAM, BAD_ID_VALUE);
    }

    /**
     * Clear controller data
     */
    void clearData() {
        controllerData.deleteAllData(TEST_COLLECTION_URI_ONE_ID);
        controllerData.deleteAllData(TEST_COLLECTION_URI_TWO_IDS);
    }

    /**
     * Create error answer from controller with info about key not found
     *
     * @param key - key of data
     * @return - json with description error
     */
    ObjectNode createKeyNotFoundError(String key) {
        ObjectNode result = jsonUtils.createJson();
        jsonUtils.putString(result, FakeController.DESCRIPTION_PARAM, String.format(FakeController.KEY_NOT_FOUND, key));
        return result;
    }

    /**
     * Create error answer from controller with info bad request
     *
     * @return - json with description error
     */
    ObjectNode createCudBadRequest() {
        ObjectNode result = jsonUtils.createJson();
        jsonUtils.putString(result, FakeController.DESCRIPTION_PARAM, FakeModifyController.BAD_REQUEST);
        return result;
    }

    HttpServletRequest createRequest(RequestMethod method, String body) {
        return createRequest(method, body, null, null);
    }

    HttpServletRequest createRequestVariables(RequestMethod method, String body, Map<String, String> uriVariables) {
        return createRequest(method, body, null, uriVariables);
    }

    HttpServletRequest createRequestHeaders(RequestMethod method, String body, Map<String, String> headers) {
        return createRequest(method, body, headers, null);
    }

    /**
     * Create http request to send to controller
     *
     * @param method - request method
     * @param body - body of request
     * @param headers - http headers
     * @param uriVariables - uri variables with id. example: id - id-value
     * @return - http request
     */
    private HttpServletRequest createRequest(RequestMethod method, String body, Map<String, String> headers, Map<String, String> uriVariables) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(method.name());
        if (body != null) {
            request.setContent(body.getBytes());
        }
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (uriVariables != null) {
            request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriVariables);
        }
        return request;
    }

    /**
     * Handle response and assert {@link FakeController#delay()} called
     *
     * @param fakeController - controller
     * @param request - http request
     * @param delayMs - delay time
     * @return - response
     */
    ResponseEntity<String> handleResponse(FakeController fakeController, HttpServletRequest request, long delayMs) {
        fakeController = Mockito.spy(fakeController);

        long now = System.currentTimeMillis();
        ResponseEntity<String> response = fakeController.handle(request);
        assertTrue(System.currentTimeMillis() - now >= delayMs);

        verify(fakeController, times(1)).delay();
        if (delayMs > 0) {
            verify(systemUtils, times(1)).sleep(delayMs);
        } else {
            verify(systemUtils, never()).sleep(anyLong());
        }
        return response;
    }
}
