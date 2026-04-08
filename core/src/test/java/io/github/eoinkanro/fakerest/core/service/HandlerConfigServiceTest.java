package io.github.eoinkanro.fakerest.core.service;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.Config;
import io.github.eoinkanro.fakerest.core.conf.ConfigLoader;
import io.github.eoinkanro.fakerest.core.conf.LoadConfigException;
import io.github.eoinkanro.fakerest.core.conf.SaveConfigException;
import io.github.eoinkanro.fakerest.core.conf.impl.StaticHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerFactory;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerRegistry;
import io.github.eoinkanro.fakerest.core.handler.RegisterException;
import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandlerConfigServiceTest {

    @Mock
    private HttpHandlerRegistry handlerRegistry;
    @Mock
    private HttpHandlerFactory handlerFactory;
    @Mock
    private ConfigLoader configLoader;
    @Mock
    private HttpHandler handler;
    @Mock
    private HttpHandler newHandler;
    @Mock
    private AbstractHttpHandlerConfig handlerConfig;
    @Mock
    private Set<AbstractHttpHandlerConfig> handlersSet;
    @Mock
    private Config config;

    @InjectMocks
    private HandlerConfigService subject;

    // ── addHandler ────────────────────────────────────────────

    @Test
    @SneakyThrows
    void addHandler_success_returnsId() {
        String id = UUID.randomUUID().toString();
        when(handlerConfig.getId()).thenReturn(id);
        when(handlerFactory.create(handlerConfig)).thenReturn(handler);
        when(configLoader.loadOrGetCached()).thenReturn(config);
        when(config.getHandlers()).thenReturn(handlersSet);

        String result = subject.addHandler(handlerConfig);

        assertEquals(id, result);
        verify(handlerConfig).initId();
        verify(handlerRegistry).register(handler);
        verify(handlersSet).add(handlerConfig);
        verify(configLoader).save(config);
    }

    @Test
    @SneakyThrows
    void addHandler_registryThrows_propagatesException() {
        when(handlerFactory.create(handlerConfig)).thenReturn(handler);
        doThrow(RegisterException.class).when(handlerRegistry).register(handler);

        assertThrows(RegisterException.class, () -> subject.addHandler(handlerConfig));
        verify(configLoader, never()).save(any());
    }

    // ── deleteHandler ─────────────────────────────────────────

    @Test
    @SneakyThrows
    void deleteHandler_notFound_returnsFalse() {
        String id = UUID.randomUUID().toString();
        when(handlerRegistry.find(id)).thenReturn(null);

        boolean result = subject.deleteHandler(id);

        assertFalse(result);
        verify(handlerRegistry, never()).unregister(any(), any());
        verify(configLoader, never()).save(any());
    }

    @Test
    @SneakyThrows
    void deleteHandler_found_unregistersAndSaves() {
        String id = UUID.randomUUID().toString();
        HttpMethod method = HttpMethod.GET;
        String path = UUID.randomUUID().toString();

        when(handlerRegistry.find(id)).thenReturn(handler);
        when(handler.getConfig()).thenReturn(handlerConfig);
        when(handlerConfig.getMethod()).thenReturn(method);
        when(handlerConfig.getPath()).thenReturn(path);
        when(configLoader.loadOrGetCached()).thenReturn(config);
        when(config.getHandlers()).thenReturn(handlersSet);
        when(handlersSet.stream()).thenReturn(Stream.of());

        boolean result = subject.deleteHandler(id);

        assertTrue(result);
        verify(handlerRegistry).unregister(method, path);
        verify(config).setHandlers(any());
        verify(configLoader).save(config);
    }

    // ── updateHandler ─────────────────────────────────────────

    @Test
    @SneakyThrows
    void updateHandler_notFound_returnsFalse() {
        String id = UUID.randomUUID().toString();
        when(handlerConfig.getId()).thenReturn(id);
        when(handlerRegistry.find(id)).thenReturn(null);

        boolean result = subject.updateHandler(handlerConfig);

        assertFalse(result);
        verify(handlerRegistry, never()).unregister(any(), any());
        verify(configLoader, never()).save(any());
    }

    @Test
    @SneakyThrows
    void updateHandler_success_returnsTrue() {
        String id = UUID.randomUUID().toString();
        HttpMethod oldMethod = HttpMethod.GET;
        String oldPath = "/" + UUID.randomUUID();
        HttpMethod newMethod = HttpMethod.POST;
        String newPath = "/" + UUID.randomUUID();

        AbstractHttpHandlerConfig oldConfig = mock(AbstractHttpHandlerConfig.class);
        when(oldConfig.getMethod()).thenReturn(oldMethod);
        when(oldConfig.getPath()).thenReturn(oldPath);
        when(oldConfig.getId()).thenReturn(id);

        when(handlerConfig.getId()).thenReturn(id);
        when(handlerConfig.getMethod()).thenReturn(newMethod);
        when(handlerConfig.getPath()).thenReturn(newPath);

        when(handlerRegistry.find(id)).thenReturn(handler);
        when(handler.getConfig()).thenReturn(oldConfig);
        when(handlerRegistry.find(newMethod, newPath)).thenReturn(null);
        when(handlerFactory.create(handlerConfig)).thenReturn(newHandler);
        when(configLoader.loadOrGetCached()).thenReturn(config);
        when(config.getHandlers()).thenReturn(handlersSet);
        when(handlersSet.stream()).thenReturn(Stream.of(StaticHttpHandlerConfig.builder().id(id).build()));

        boolean result = subject.updateHandler(handlerConfig);

        assertTrue(result);
        verify(handlerRegistry).unregister(oldMethod, oldPath);
        verify(handlerRegistry).register(newHandler);
        verify(config).setHandlers(any());
        verify(handlersSet).add(handlerConfig);
        verify(configLoader).save(config);
    }

    @Test
    @SneakyThrows
    void updateHandler_newPathConflicts_reRegistersOldAndReturnsFalse() {
        String id = UUID.randomUUID().toString();
        HttpMethod oldMethod = HttpMethod.GET;
        String oldPath = "/" + UUID.randomUUID();
        HttpMethod newMethod = HttpMethod.POST;
        String newPath = "/" + UUID.randomUUID();

        AbstractHttpHandlerConfig oldConfig = mock(AbstractHttpHandlerConfig.class);
        when(oldConfig.getMethod()).thenReturn(oldMethod);
        when(oldConfig.getPath()).thenReturn(oldPath);

        when(handlerConfig.getId()).thenReturn(id);
        when(handlerConfig.getMethod()).thenReturn(newMethod);
        when(handlerConfig.getPath()).thenReturn(newPath);

        when(handlerRegistry.find(id)).thenReturn(handler);
        when(handler.getConfig()).thenReturn(oldConfig);
        when(handlerRegistry.find(newMethod, newPath)).thenReturn(mock(HttpHandler.class));
        when(handlerFactory.create(handlerConfig)).thenReturn(newHandler);

        boolean result = subject.updateHandler(handlerConfig);

        assertFalse(result);
        verify(handlerRegistry).unregister(oldMethod, oldPath);
        verify(handlerRegistry).register(handler);
        verify(configLoader, never()).save(any());
    }

    // ── updateMainConfig ──────────────────────────────────────

    @Test
    @SneakyThrows
    void updateMainConfig_savesUpdatedPorts() {
        int mockPort = 9090;
        int uiPort = 9091;

        Config newConfig = mock(Config.class);
        Config inputConfig = mock(Config.class);
        Config cachedConfig = mock(Config.class);
        Config.ConfigBuilder builder = mock(Config.ConfigBuilder.class);

        when(inputConfig.getMockPort()).thenReturn(mockPort);
        when(inputConfig.getUiPort()).thenReturn(uiPort);
        when(configLoader.loadOrGetCached()).thenReturn(cachedConfig);
        when(cachedConfig.toBuilder()).thenReturn(builder);
        when(builder.mockPort(mockPort)).thenReturn(builder);
        when(builder.uiPort(uiPort)).thenReturn(builder);
        when(builder.build()).thenReturn(newConfig);

        boolean result = subject.updateMainConfig(inputConfig);

        assertTrue(result);
        verify(configLoader).save(newConfig);
    }

    // ── getConfig ─────────────────────────────────────────────

    @Test
    @SneakyThrows
    void getConfig_returnsLoadedConfig() {
        when(configLoader.loadOrGetCached()).thenReturn(config);

        Config result = subject.getConfig();

        assertSame(config, result);
    }

}
