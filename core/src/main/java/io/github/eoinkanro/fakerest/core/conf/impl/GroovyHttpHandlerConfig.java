package io.github.eoinkanro.fakerest.core.conf.impl;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.HttpHandlerType;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class GroovyHttpHandlerConfig extends AbstractHttpHandlerConfig {

    @NonNull
    private String groovyCode;

    @Override
    public HttpHandlerType getType() {
        return HttpHandlerType.GROOVY;
    }

}
