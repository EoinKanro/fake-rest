package io.github.eoinkanro.fakerest.ui.handler;

import io.github.eoinkanro.fakerest.core.conf.AbstractHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.conf.HttpHandlerConfigDeserializer;
import io.github.eoinkanro.fakerest.core.service.HandlerConfigService;
import io.javalin.http.Context;
import jakarta.inject.Singleton;

@Singleton
public class UpdateHttpHandlerApiHandler extends AbstractApiHandler {

    public UpdateHttpHandlerApiHandler(HandlerConfigService handlerConfigService, HttpHandlerConfigDeserializer handlerConfigDeserializer) {
        super(handlerConfigService, handlerConfigDeserializer);
    }

    @Override
    protected void process(Context context) throws Exception {
        AbstractHttpHandlerConfig config = handlerConfigDeserializer.deserialize(context.body());

        if (!handlerConfigService.updateHandler(config)) {
            throw new ApiHandlerInternalErrorException();
        }
    }

}
