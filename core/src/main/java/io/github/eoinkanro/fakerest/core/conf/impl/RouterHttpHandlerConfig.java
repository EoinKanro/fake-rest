package io.github.eoinkanro.fakerest.core.conf.impl;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.HttpHandlerType;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class RouterHttpHandlerConfig extends AbstractHttpHandlerConfig {

    @NonNull
    private final String routerPath;

    @Override
    public HttpHandlerType getType() {
        return HttpHandlerType.ROUTER;
    }

}
