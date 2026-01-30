package io.github.eoinkanro.fakerest.core.conf;

import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractHttpHandlerConfig {

    private final String path;
    private final HttpMethod method;

    public abstract HttpHandlerType getType();

}
