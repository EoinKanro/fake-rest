package io.github.eoinkanro.fakerest.core.controller;

import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import io.undertow.server.HttpServerExchange;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RouterControllerTest extends FakeControllerTest {

    private static final String TEST_URL = "http://localhost";
    private static final String TEST_URI_WITH_SLASH = "/test";
    private static final String TEST_URI_WITHOUT_SLASH = "test";
    private static final String HEADER_NAME = "header-name";
    private static final String HEADER_VALUE = "header-value";

    static Stream<String> provideDifferentUri() {
        return Stream.of(TEST_URI_WITH_SLASH, TEST_URI_WITHOUT_SLASH);
    }

    static Stream<Arguments> provideAllMethodsBodyHeaders() {
        HttpMethod[] requestMethods = HttpMethod.values();
        Arguments[] arguments = new Arguments[requestMethods.length * 4];
        for (int i = 0; i < arguments.length; i++) {
            int methodIndex = i / 4;

            int reminder = i % 4;
            String body = reminder > 1 ? REQUEST_BODY : "";
            Map<String, String> headers = reminder == 1 || reminder == 3 ?
                    Collections.singletonMap(HEADER_NAME, HEADER_VALUE) : null;

            arguments[i] = Arguments.of(requestMethods[methodIndex], body, headers);
        }
        return Stream.of(arguments);
    }

//    @InjectMocks
//    private RestClient restClient;

    private String urlWithPort;

//    @SneakyThrows
//    @BeforeEach
//    void init() {
//        super.init();
//        Field restTemplate = restClient.getClass().getDeclaredField("restTemplate");
//        restTemplate.setAccessible(true);
//        mockServer = MockRestServiceServer.createServer((RestTemplate) restTemplate.get(restClient));
//        restTemplate.setAccessible(false);
//        urlWithPort = TEST_URL + ":" + serverProperties.getPort();
//    }

    @ParameterizedTest
    @MethodSource("provideDifferentUri")
    void buildUri_SendUri_ReturnUrl(String uri) throws URISyntaxException {
        RouterController subj = testControllersFabric.createRouterController(TEST_COLLECTION_URI_ONE_ID, uri, HttpMethod.GET);
        HttpServerExchange request = createRequest(HttpMethod.GET, TEST_COLLECTION_URI_ONE_ID);
        URI response = invokeBuildUri(subj, request);

        String expectedUrl = "http://localhost:" + request.getHostPort();
        if (uri.startsWith("/")) {
            expectedUrl += uri;
        } else {
            expectedUrl += "/" + uri;
        }

        assertEquals(new URI(expectedUrl), response);
    }

    @Test
    void buildUri_SendUrl_ReturnUrl() throws URISyntaxException {
        RouterController subj = testControllersFabric.createRouterController(TEST_COLLECTION_URI_ONE_ID, TEST_URL, HttpMethod.GET);
        HttpServerExchange request = createRequest(HttpMethod.GET, TEST_COLLECTION_URI_ONE_ID);
        URI response = invokeBuildUri(subj, request);
        assertEquals(new URI(TEST_URL), response);
    }

//    @ParameterizedTest
//    @MethodSource("provideAllMethodsBodyHeaders")
//    void sendRequest_ReturnOk(HttpMethod method, String body, Map<String, String> headers) {
//        RouterController subj = testControllersFabric.createRouterController(TEST_STATIC_URI, urlWithPort, method);
//
//        if (headers == null) {
//            mockServer.expect(requestTo(urlWithPort))
//                    .andExpect(method(HttpMethod.valueOf(method.name())))
//                    .andExpect(content().string(body))
//                    .andRespond(withSuccess());
//        } else {
//            mockServer.expect(requestTo(urlWithPort))
//                    .andExpect(method(HttpMethod.valueOf(method.name())))
//                    .andExpect(content().string(body))
//                    .andExpect(header(HEADER_NAME, HEADER_VALUE))
//                    .andRespond(withSuccess());
//        }
//
//        HttpServerExchange request = createRequestWithHeaders(HttpMethod.GET, body, headers);
//        subj.handle(request);
//        mockServer.verify();
//    }
//
//    @ParameterizedTest
//    @MethodSource("provideAllMethods")
//    void sendRequest_BadConfigToUrl_InternalError(HttpMethod method) {
//        RouterController subj = testControllersFabric.createRouterController(TEST_STATIC_URI, null, method);
//        HttpServerExchange request = createRequest(method, null);
//        ResponseEntity<String> response = subj.handle(request);
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    @ParameterizedTest
//    @MethodSource("provideAllMethods")
//    void sendRequest_BadConfigMethod_InternalError(HttpMethod method) {
//        RouterController subj = testControllersFabric.createRouterController(TEST_STATIC_URI, null, null);
//        HttpServerExchange request = createRequest(method, null);
//        ResponseEntity<String> response = subj.handle(request);
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }

    @SneakyThrows
    private URI invokeBuildUri(RouterController subj, HttpServerExchange request) {
        Method buildUri = RouterController.class.getDeclaredMethod("buildUri", HttpServerExchange.class);
        buildUri.setAccessible(true);
        URI result = (URI) buildUri.invoke(subj, request);
        buildUri.setAccessible(false);
        return result;
    }

}
