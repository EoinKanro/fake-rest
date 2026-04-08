package io.github.eoinkanro.fakerest.core.handler.impl;

import io.github.eoinkanro.fakerest.core.conf.impl.GroovyHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerDataRepository;
import io.github.eoinkanro.fakerest.core.model.HttpRequest;
import io.github.eoinkanro.fakerest.core.model.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroovyHttpHandlerTest {

    @Mock
    private GroovyHttpHandlerConfig config;
    @Mock
    private HttpHandlerDataRepository dataRepository;
    @Mock
    private HttpRequest request;

    @Test
    void testHandle() {
        String groovyCode = """
            ObjectNode json = jsonMapper.readValue("{\\"key\\":\\"value\\"}", ObjectNode.class);
            String value = json.get("key").asString();
            dataRepository.put(value, json);
            request.getBody();
            return HttpResponse.builder().code(200).build();
            """;

        when(config.getGroovyCode()).thenReturn(groovyCode);

        GroovyHttpHandler subject = new GroovyHttpHandler(config, dataRepository);
        HttpResponse response = subject.handle(request);

        assertEquals(200, response.getCode());
        verify(dataRepository).put(eq("value"), any());
        verify(request).getBody();
    }

}
