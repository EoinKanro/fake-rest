package io.github.eoinkanro.fakerest.core.conf;

import io.github.eoinkanro.fakerest.core.conf.impl.GroovyHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.RouterHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.StaticHttpHandlerConfig;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.node.ObjectNode;

@Singleton
public class HttpHandlerConfigDeserializer extends ValueDeserializer<AbstractHttpHandlerConfig> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AbstractHttpHandlerConfig deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        return deserialize((ObjectNode) p.readValueAsTree());
    }

    public AbstractHttpHandlerConfig deserialize(String config) {
        return deserialize(objectMapper.readValue(config, ObjectNode.class));
    }

    private AbstractHttpHandlerConfig deserialize(ObjectNode node) {
        HttpHandlerType type = HttpHandlerType.valueOf(node.get("type").asString());

        return switch (type) {
            case STATIC -> objectMapper.treeToValue(node, StaticHttpHandlerConfig.class);
            case GROOVY -> objectMapper.treeToValue(node, GroovyHttpHandlerConfig.class);
            case ROUTER -> objectMapper.treeToValue(node, RouterHttpHandlerConfig.class);
        };
    }

}
