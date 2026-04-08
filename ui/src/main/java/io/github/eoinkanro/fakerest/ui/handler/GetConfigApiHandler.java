package io.github.eoinkanro.fakerest.ui.handler;

import io.github.eoinkanro.fakerest.core.conf.Config;
import io.github.eoinkanro.fakerest.core.conf.HttpHandlerConfigDeserializer;
import io.github.eoinkanro.fakerest.core.service.HandlerConfigService;
import io.javalin.http.Context;
import jakarta.inject.Singleton;

@Singleton
public class GetConfigApiHandler extends AbstractApiHandler{

    public GetConfigApiHandler(HandlerConfigService handlerConfigService, HttpHandlerConfigDeserializer handlerConfigDeserializer) {
        super(handlerConfigService, handlerConfigDeserializer);
    }

    @Override
    protected void process(Context context) throws Exception {
        Config config = handlerConfigService.getConfig();
        context.result(objectMapper.writeValueAsString(config));
    }

}
