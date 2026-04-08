package io.github.eoinkanro.fakerest.core.conf;

import io.github.eoinkanro.fakerest.core.model.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractHttpHandlerConfig {

    private String id;
    @EqualsAndHashCode.Include
    private String path;
    @EqualsAndHashCode.Include
    private HttpMethod method;

    public abstract HttpHandlerType getType();

    public void initId() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
    }

}

