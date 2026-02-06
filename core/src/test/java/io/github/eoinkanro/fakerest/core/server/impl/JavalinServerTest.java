package io.github.eoinkanro.fakerest.core.server.impl;

import io.github.eoinkanro.fakerest.core.conf.Config;
import io.github.eoinkanro.fakerest.core.conf.ConfigLoader;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.model.HttpRequest;
import io.github.eoinkanro.fakerest.core.model.HttpResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JavalinServerTest {

    @Mock
    private ConfigLoader configLoader;
    @Mock
    private Config config;
    @Mock
    private HttpHandler handler;
    @Mock
    private HttpResponse response;
    @Mock
    private HttpHandlerRegistry registry;

    @InjectMocks
    private JavalinServer subject;

    @Test
    @SneakyThrows
    void test() {
        HttpClient client = HttpClient.newHttpClient();

        int port = getFreePort();
        String path = "/path";
        URI requestUri = URI.create("http://localhost:" + port + "/path?key=value");

        String body = UUID.randomUUID().toString();
        Map<String, String> queryMap = Map.of("key", "value");

        HttpRequest requestWithBody = HttpRequest.builder()
                .body(body)
                .build();
        requestWithBody.getVariables().putAll(queryMap);

        HttpRequest requestWithoutBody = HttpRequest.builder()
                .body("")
                .build();
        requestWithoutBody.getVariables().putAll(queryMap);

        when(configLoader.loadOrGetCached()).thenReturn(config);
        when(config.getMockPort()).thenReturn(port);
        when(registry.find(any(), eq(path))).thenReturn(handler);
        when(handler.handle(any())).thenReturn(response);
        when(response.getBody()).thenReturn(body);
        when(response.getCode()).thenReturn(200);

        subject.init();

        //test GET without body and with query variables
        java.net.http.HttpRequest requestToServer = java.net.http.HttpRequest.newBuilder()
            .uri(requestUri)
            .GET()
            .build();

        java.net.http.HttpResponse<String> response =
            client.send(requestToServer, java.net.http.HttpResponse.BodyHandlers.ofString());

        verify(handler).handle(eq(requestWithoutBody));
        assertEquals(body, response.body());

        //test POST with body and query variables
        requestToServer = java.net.http.HttpRequest.newBuilder()
            .uri(requestUri)
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(body))
            .build();

        response = client.send(requestToServer,
            java.net.http.HttpResponse.BodyHandlers.ofString());

        verify(handler).handle(eq(requestWithBody));
        assertEquals(body, response.body());
    }

    @SneakyThrows
    private int getFreePort() {
        try (var socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

}
