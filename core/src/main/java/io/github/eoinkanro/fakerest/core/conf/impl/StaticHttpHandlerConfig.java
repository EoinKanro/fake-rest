package io.github.eoinkanro.fakerest.core.conf.impl;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.HttpHandlerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StaticHttpHandlerConfig extends AbstractHttpHandlerConfig {

    private String responseBody;
    private int responseCode;

    @Override
    public HttpHandlerType getType() {
        return HttpHandlerType.STATIC;
    }

}
