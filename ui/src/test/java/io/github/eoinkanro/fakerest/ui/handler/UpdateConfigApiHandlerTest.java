package io.github.eoinkanro.fakerest.ui.handler;

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
class UpdateConfigApiHandlerTest {

    @Mock
    private HandlerConfigService handlerConfigService;
    @Mock
    private Context context;

    @InjectMocks
    private UpdateConfigApiHandler subject;

    @Test
    @SneakyThrows
    void handle_success_setsStatus200() {
        when(context.body()).thenReturn("{\"mockPort\":9090,\"uiPort\":9091}");
        when(handlerConfigService.updateMainConfig(any())).thenReturn(true);

        subject.handle(context);

        verify(context).status(200);
    }

    @Test
    @SneakyThrows
    void handle_updateReturnsFalse_sets500() {
        when(context.body()).thenReturn("{\"mockPort\":9090,\"uiPort\":9091}");
        when(handlerConfigService.updateMainConfig(any())).thenReturn(false);

        subject.handle(context);

        verify(context).status(500);
    }

    @Test
    @SneakyThrows
    void handle_invalidBody_sets500() {
        when(context.body()).thenReturn("not-json");

        subject.handle(context);

        verify(context).status(500);
        verify(handlerConfigService, never()).updateMainConfig(any());
    }

}
