package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.commons.utils.SystemUtils;
import io.github.eoinkanro.fakerest.core.conf.server.controller.ControllerData;
import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import io.github.eoinkanro.fakerest.core.utils.HttpUtils;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import lombok.SneakyThrows;
import org.apache.tools.ant.filters.StringInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.io.BufferedInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

abstract class FakeControllerTest {
    private static final long DELAY_MS = 10;
    static final String TEST_STATIC_URI = "/test";
    static final String TEST_COLLECTION_URI_ONE_ID = "/test/{id}";
    static final String TEST_COLLECTION_URI_TWO_IDS = "/test/{id}/{id2}";
    static final String REQUEST_BODY = "body";
    static final String EMPTY_REQUEST_BODY = "";
    static final String EMPTY_JSON_BODY = "{}";

    static final String FIRST_ID_PARAM="id";
    static final String FIRST_ID_VALUE ="id-value";
    static final String SECOND_ID_PARAM="id2";
    static final String SECOND_ID_VALUE="id2-value";
    static final String BAD_ID_VALUE="bad-value";
    static final String DATA_PARAM="data";
    static final String FIRST_DATA_VALUE ="data-value";
    static final String SECOND_DATA_VALUE ="data-value2";

    @InjectMocks
    TestControllersFabric testControllersFabric;
    @Spy
    ControllerData controllerData;
    MockedStatic<SystemUtils> mockedStatic;

    ObjectNode JSON_NO_ID;
    ObjectNode JSON_ONE_ID_FIRST;
    ObjectNode JSON_ONE_ID_FIRST_BAD;
    ObjectNode JSON_ONE_ID_SECOND;
    ObjectNode JSON_ONE_ID_EMPTY_ID;
    ObjectNode JSON_TWO_ID;
    ObjectNode JSON_TWO_ID_BAD;
    ObjectNode JSON_TWO_ID_EMPTY_ID;

    @BeforeEach
    void init() {
        this.mockedStatic = Mockito.mockStatic(SystemUtils.class);
    }

    @AfterEach
    void end() {
        this.mockedStatic.close();
    }

    private static Stream<Arguments> provideAllMethodsDelay(long delayMs) {
        HttpMethod[] requestMethods = HttpMethod.values();
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

    static Stream<HttpMethod> provideAllMethods() {
        return Stream.of(HttpMethod.values());
    }

    /**
     * Init and fill controller data
     */
    void fillData() {
        clearData();
        initData();
        String firstKey = controllerData.buildKey(JSON_ONE_ID_FIRST, Collections.singletonList(FIRST_ID_PARAM));
        controllerData.putData(TEST_COLLECTION_URI_ONE_ID, firstKey, JSON_ONE_ID_FIRST);

        String secondKey = controllerData.buildKey(JSON_ONE_ID_SECOND, Collections.singletonList(FIRST_ID_PARAM));
        controllerData.putData(TEST_COLLECTION_URI_ONE_ID, secondKey, JSON_ONE_ID_SECOND);

        String keyTwo = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        controllerData.putData(TEST_COLLECTION_URI_TWO_IDS, keyTwo, JSON_TWO_ID);
    }

    /**
     * Init controller data
     */
    void initData() {
        JSON_ONE_ID_FIRST = JsonUtils.createJson();
        JsonUtils.putString(JSON_ONE_ID_FIRST, FIRST_ID_PARAM, FIRST_ID_VALUE);
        JsonUtils.putString(JSON_ONE_ID_FIRST, DATA_PARAM, FIRST_DATA_VALUE);

        JSON_ONE_ID_SECOND = JsonUtils.createJson();
        JsonUtils.putString(JSON_ONE_ID_SECOND, FIRST_ID_PARAM, SECOND_ID_VALUE);
        JsonUtils.putString(JSON_ONE_ID_SECOND, DATA_PARAM, SECOND_DATA_VALUE);

        JSON_TWO_ID = JsonUtils.createJson();
        JsonUtils.putString(JSON_TWO_ID, FIRST_ID_PARAM, FIRST_ID_VALUE);
        JsonUtils.putString(JSON_TWO_ID, SECOND_ID_PARAM, SECOND_ID_VALUE);
        JsonUtils.putString(JSON_TWO_ID, DATA_PARAM, FIRST_DATA_VALUE);

        JSON_ONE_ID_FIRST_BAD = JsonUtils.createJson();
        JsonUtils.putString(JSON_ONE_ID_FIRST_BAD, FIRST_ID_PARAM, BAD_ID_VALUE);

        JSON_TWO_ID_BAD = JsonUtils.createJson();
        JsonUtils.putString(JSON_TWO_ID_BAD, FIRST_ID_PARAM, BAD_ID_VALUE);
        JsonUtils.putString(JSON_TWO_ID_BAD, SECOND_ID_PARAM, BAD_ID_VALUE);

        JSON_NO_ID = JsonUtils.createJson();
        JsonUtils.putString(JSON_NO_ID, DATA_PARAM, FIRST_DATA_VALUE);

        JSON_ONE_ID_EMPTY_ID = JsonUtils.createJson();
        JsonUtils.putString(JSON_ONE_ID_EMPTY_ID, DATA_PARAM, FIRST_DATA_VALUE);
        JsonUtils.putString(JSON_ONE_ID_EMPTY_ID, FIRST_ID_PARAM, EMPTY_REQUEST_BODY);

        JSON_TWO_ID_EMPTY_ID = JsonUtils.createJson();
        JsonUtils.putString(JSON_TWO_ID_EMPTY_ID, DATA_PARAM, FIRST_DATA_VALUE);
        JsonUtils.putString(JSON_TWO_ID_EMPTY_ID, FIRST_ID_PARAM, EMPTY_REQUEST_BODY);
        JsonUtils.putString(JSON_TWO_ID_EMPTY_ID, SECOND_ID_PARAM, EMPTY_REQUEST_BODY);
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
        ObjectNode result = JsonUtils.createJson();
        JsonUtils.putString(result, FakeController.DESCRIPTION_PARAM, String.format(FakeController.KEY_NOT_FOUND, key));
        return result;
    }

    /**
     * Create error answer from controller with info bad request
     *
     * @return - json with description error
     */
    ObjectNode createBadRequest(String description) {
        ObjectNode result = JsonUtils.createJson();
        JsonUtils.putString(result, FakeController.DESCRIPTION_PARAM, description);
        return result;
    }

    HttpServerExchange createRequest(HttpMethod method, String body) {
        return createRequest(method, body, null, null);
    }

    HttpServerExchange createRequestWithUriVariables(HttpMethod method, String body, String requestUri) {
        return createRequest(method, body, null, requestUri);
    }

    HttpServerExchange createRequestWithHeaders(HttpMethod method, String body, Map<String, String> headers) {
        return createRequest(method, body, headers, null);
    }

    /**
     * Create http request to send to controller
     *
     * @return - http request
     */
    //TODO
    @SneakyThrows
    private HttpServerExchange createRequest(HttpMethod method, String body, Map<String, String> headers, String requestUri) {
        HttpServerExchange request = mock(HttpServerExchange.class);
        lenient().when(request.getRequestMethod()).thenReturn(new HttpString(method.name()));
        if (body != null) {
            lenient().when(request.getInputStream()).thenReturn(new BufferedInputStream(new StringInputStream(body)));
        }

        if (headers != null) {
            HeaderMap headerMap = new HeaderMap();

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerMap.add(new HttpString(entry.getKey()), entry.getValue());
            }
            lenient().when(request.getRequestHeaders()).thenReturn(headerMap);
        }
        lenient().when(request.getRequestURI()).thenReturn(requestUri);

        if (requestUri != null) {
            lenient().when(request.getAttachment(PathTemplateMatch.ATTACHMENT_KEY)).thenReturn(new PathTemplateMatch(requestUri, getIdValues(requestUri)));
        }
        lenient().when(request.getHostPort()).thenReturn(8080);
        lenient().when(request.getRequestScheme()).thenReturn("http");
        lenient().when(request.getHostName()).thenReturn("localhost");

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
    ControllerResponse handleResponse(FakeController fakeController, HttpServerExchange request, long delayMs) {
        fakeController = Mockito.spy(fakeController);

        ControllerResponse response = fakeController.handle(request);

        verify(fakeController, times(1)).delay();
        if (delayMs > 0) {
            mockedStatic.verify(() -> SystemUtils.sleep(delayMs), times(1));
        } else {
            mockedStatic.verify(() -> SystemUtils.sleep(anyLong()), times(0));
        }
        return response;
    }

    String createRequestUri(String... args) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(args).forEach(arg -> {
            builder.append(arg);
            builder.append(HttpUtils.URI_DELIMITER);
        });
        return builder.toString();
    }

    Map<String, String> getIdValues(String uri) {
        String[] splitUri = uri.split(HttpUtils.URI_DELIMITER);
        if (splitUri.length % 2 > 0) {
            throw new RuntimeException("Bad uri " + uri);
        }

        Map<String, String> result = new HashMap<>();

        String key = null;
        for (int i = 0; i < splitUri.length; i++) {
            if (i % 2 == 0) {
                key = splitUri[i];
            } else {
                result.put(key, splitUri[i]);
            }
        }
        return result;
    }
}
