package io.github.eoinkanro.fakerest.core.conf;

import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractHttpHandlerConfig {

    private String path;
    private HttpMethod method;

    public abstract HttpHandlerType getType();

}

