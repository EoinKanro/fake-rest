package io.github.eoinkanro.fakerest.ui.handler;

import io.github.eoinkanro.fakerest.core.service.HandlerConfigService;
import io.javalin.http.Context;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteHttpHandlerApiHandlerTest {

    @Mock
    private HandlerConfigService handlerConfigService;
    @Mock
    private Context context;

    @InjectMocks
    private DeleteHttpHandlerApiHandler subject;

    @Test
    @SneakyThrows
    void handle_success_setsStatus200() {
        String id = UUID.randomUUID().toString();

        when(context.pathParam("id")).thenReturn(id);
        when(handlerConfigService.deleteHandler(id)).thenReturn(true);

        subject.handle(context);

        verify(context).status(200);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @SneakyThrows
    void handle_blankId_sets400(String id) {
        when(context.pathParam("id")).thenReturn(id);

        subject.handle(context);

        verify(context).status(400);
        verify(handlerConfigService, never()).deleteHandler(any());
    }

    @Test
    @SneakyThrows
    void handle_deleteHandlerReturnsFalse_sets500() {
        String id = UUID.randomUUID().toString();

        when(context.pathParam("id")).thenReturn(id);
        when(handlerConfigService.deleteHandler(id)).thenReturn(false);

        subject.handle(context);

        verify(context).status(500);
    }

    @Test
    @SneakyThrows
    void handle_deleteHandlerThrows_sets500() {
        String id = UUID.randomUUID().toString();

        when(context.pathParam("id")).thenReturn(id);
        when(handlerConfigService.deleteHandler(id)).thenThrow(new RuntimeException());

        subject.handle(context);

        verify(context).status(500);
    }

}
