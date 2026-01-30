package io.github.eoinkanro.fakerest.core.conf.impl;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.HttpHandlerType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class StaticHttpHandlerConfig extends AbstractHttpHandlerConfig {

    private String responseBody;
    private int responseCode;

    @Override
    public HttpHandlerType getType() {
        return HttpHandlerType.STATIC;
    }

}
