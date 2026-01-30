package io.github.eoinkanro.fakerest.core.conf;

import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractHttpHandlerConfig {

    @NonNull
    private final String path;
    @NonNull
    private final HttpMethod method;

    public abstract HttpHandlerType getType();

}
