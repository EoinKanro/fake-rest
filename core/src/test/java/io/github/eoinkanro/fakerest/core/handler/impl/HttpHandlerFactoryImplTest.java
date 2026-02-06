package io.github.eoinkanro.fakerest.core.handler.impl;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.GroovyHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.RouterHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.StaticHttpHandlerConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class HttpHandlerFactoryImplTest {

    @InjectMocks
    private HttpHandlerFactoryImpl subject;

    @ParameterizedTest
    @MethodSource
    void testCreate(AbstractHttpHandlerConfig config, Class handlerClass) {
        assertTrue(subject.create(config).getClass().isAssignableFrom(handlerClass));
    }

    private static Object[][] testCreate() {
        return new Object[][] {
            new Object[] {
                StaticHttpHandlerConfig.builder().build(),
                StaticHttpHandler.class
            },
            new Object[] {
                GroovyHttpHandlerConfig.builder().build(),
                GroovyHttpHandler.class
            },
            new Object[] {
                RouterHttpHandlerConfig.builder().build(),
                RouterHttpHandler.class
            }
        };
    }

}
