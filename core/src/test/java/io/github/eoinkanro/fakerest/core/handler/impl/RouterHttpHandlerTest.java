package io.github.eoinkanro.fakerest.core.handler.impl;

import io.github.eoinkanro.fakerest.core.conf.impl.RouterHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import io.github.eoinkanro.fakerest.core.model.HttpRequest;
import io.github.eoinkanro.fakerest.core.model.HttpResponse;
import io.javalin.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouterHttpHandlerTest {

    @Mock
    private RouterHttpHandlerConfig config;
    @Mock
    private HttpHandlerRegistry registry;

    @InjectMocks
    private RouterHttpHandler subject;

    @Test
    void testHandleNotFound() {
        HttpResponse response = subject.handle(null);
        assertEquals(HttpStatus.NOT_FOUND.getCode(), response.getCode());
    }

    @Test
    void testHandleOk() {
        String routerPath = UUID.randomUUID().toString();
        HttpHandler routeHandler = Mockito.mock(HttpHandler.class);
        HttpRequest request = HttpRequest.builder().build();

        when(config.getMethod()).thenReturn(HttpMethod.GET);
        when(config.getRouterPath()).thenReturn(routerPath);
        when(registry.find(eq(HttpMethod.GET), eq(routerPath)))
            .thenReturn(routeHandler);

        subject.handle(request);
        verify(routeHandler).handle(request);
    }

}
