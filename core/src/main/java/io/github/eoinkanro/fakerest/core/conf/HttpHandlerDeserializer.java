package io.github.eoinkanro.fakerest.core.conf;

import io.github.eoinkanro.fakerest.core.conf.impl.GroovyHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.RouterHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.impl.StaticHttpHandlerConfig;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.node.ObjectNode;

public class HttpHandlerDeserializer extends ValueDeserializer<AbstractHttpHandlerConfig> {

    @Override
    public AbstractHttpHandlerConfig deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        ObjectNode node = p.readValueAsTree();

        HttpHandlerType type = HttpHandlerType.valueOf(node.get("type").asString());

        return switch (type) {
            case STATIC -> ctxt.readTreeAsValue(node, StaticHttpHandlerConfig.class);
            case GROOVY -> ctxt.readTreeAsValue(node, GroovyHttpHandlerConfig.class);
            case ROUTER -> ctxt.readTreeAsValue(node, RouterHttpHandlerConfig.class);
        };
    }

}
