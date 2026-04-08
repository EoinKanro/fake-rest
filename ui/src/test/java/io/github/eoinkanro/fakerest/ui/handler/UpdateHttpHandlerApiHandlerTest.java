package io.github.eoinkanro.fakerest.ui.handler;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.HttpHandlerConfigDeserializer;
import io.github.eoinkanro.fakerest.core.service.HandlerConfigService;
import io.javalin.http.Context;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateHttpHandlerApiHandlerTest {

    @Mock
    private HandlerConfigService handlerConfigService;
    @Mock
    private HttpHandlerConfigDeserializer handlerConfigDeserializer;
    @Mock
    private AbstractHttpHandlerConfig handlerConfig;
    @Mock
    private Context context;

    @InjectMocks
    private UpdateHttpHandlerApiHandler subject;

    @Test
    @SneakyThrows
    void handle_success_setsStatus200() {
        String body = "{}";

        when(context.body()).thenReturn(body);
        when(handlerConfigDeserializer.deserialize(body)).thenReturn(handlerConfig);
        when(handlerConfigService.updateHandler(handlerConfig)).thenReturn(true);

        subject.handle(context);

        verify(context).status(200);
    }

    @Test
    @SneakyThrows
    void handle_updateHandlerReturnsFalse_sets500() {
        String body = "{}";

        when(context.body()).thenReturn(body);
        when(handlerConfigDeserializer.deserialize(body)).thenReturn(handlerConfig);
        when(handlerConfigService.updateHandler(handlerConfig)).thenReturn(false);

        subject.handle(context);

        verify(context).status(500);
    }

    @Test
    @SneakyThrows
    void handle_deserializerThrows_sets500() {
        String body = "invalid";

        when(context.body()).thenReturn(body);
        when(handlerConfigDeserializer.deserialize(body)).thenThrow(new RuntimeException());

        subject.handle(context);

        verify(context).status(500);
    }

}
