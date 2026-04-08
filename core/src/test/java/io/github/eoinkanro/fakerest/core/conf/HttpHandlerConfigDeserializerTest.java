package io.github.eoinkanro.fakerest.core.conf;

import io.github.eoinkanro.fakerest.core.conf.impl.GroovyHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.RouterHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.StaticHttpHandlerConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpHandlerConfigDeserializerTest {

    private static final String TYPE = "type";

    @Mock
    private JsonParser jsonParser;

    @InjectMocks
    private HttpHandlerConfigDeserializer subject;

    @ParameterizedTest
    @MethodSource
    void test(ObjectNode node, Class clz) {
        when(jsonParser.readValueAsTree()).thenReturn(node);

        AbstractHttpHandlerConfig config = subject.deserialize(jsonParser, null);
        assertEquals(clz, config.getClass());
    }

    private static Object[][] test() {
        JsonMapper mapper = JsonMapper.builder().build();

        ObjectNode staticHandler = mapper.createObjectNode();
        staticHandler.put(TYPE, HttpHandlerType.STATIC.toString());

        ObjectNode groovyHandler = mapper.createObjectNode();
        groovyHandler.put(TYPE, HttpHandlerType.GROOVY.toString());

        ObjectNode routerHandler = mapper.createObjectNode();
        routerHandler.put(TYPE, HttpHandlerType.ROUTER.toString());

        return new Object[][] {
            new Object[] { staticHandler, StaticHttpHandlerConfig.class },
            new Object[] { groovyHandler, GroovyHttpHandlerConfig.class },
            new Object[] { routerHandler, RouterHttpHandlerConfig.class }
        };
    }

}
