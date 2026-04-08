package io.github.eoinkanro.fakerest.core.handler.impl;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.handler.RegisterException;
import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpHandlerRegistryImplTest {

    @Mock
    private HttpHandler handler;
    @Mock
    private HttpHandler handler2;
    @Mock
    private AbstractHttpHandlerConfig config;
    @Mock
    private AbstractHttpHandlerConfig config2;

    @InjectMocks
    private HttpHandlerRegistryImpl subject;

    @Test
    @SneakyThrows
    void test() {
        HttpMethod method = HttpMethod.GET;
        String path = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();

        when(handler.getConfig()).thenReturn(config);
        when(handler2.getConfig()).thenReturn(config2);

        when(config.getId()).thenReturn(id);
        when(config.getMethod()).thenReturn(method);
        when(config.getPath()).thenReturn(path);

        when(config2.getMethod()).thenReturn(method);
        when(config2.getPath()).thenReturn(UUID.randomUUID().toString());

        //id null
        assertThrows(RegisterException.class, () -> subject.register(handler2));

        //cant find
        assertNull(subject.find(method, path));

        //register
        subject.register(handler);
        assertSame(handler, subject.find(method, path));

        //same id
        when(config2.getId()).thenReturn(id);
        assertThrows(RegisterException.class, () -> subject.register(handler2));

        //register again
        assertThrows(RegisterException.class, () -> subject.register(handler));

        //unregister
        subject.unregister(method, path);
        assertNull(subject.find(method, path));
    }

}
