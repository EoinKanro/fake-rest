package io.github.eoinkanro.fakerest.ui.handler;

import io.github.eoinkanro.fakerest.core.conf.Config;
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
class GetConfigApiHandlerTest {

    @Mock
    private HandlerConfigService handlerConfigService;
    @Mock
    private Context context;

    @InjectMocks
    private GetConfigApiHandler subject;

    @Test
    @SneakyThrows
    void handle_success_setsResultAndStatus200() {
        Config config = Config.builder().mockPort(8081).uiPort(8080).build();

        when(handlerConfigService.getConfig()).thenReturn(config);

        subject.handle(context);

        verify(context).result(anyString());
        verify(context).status(200);
    }

    @Test
    @SneakyThrows
    void handle_serviceThrows_sets500() {
        when(handlerConfigService.getConfig()).thenThrow(new RuntimeException());

        subject.handle(context);

        verify(context).status(500);
        verify(context, never()).result(anyString());
    }

}
