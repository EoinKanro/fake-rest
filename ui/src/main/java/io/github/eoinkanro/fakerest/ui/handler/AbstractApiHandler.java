package io.github.eoinkanro.fakerest.ui.handler;

import io.avaje.inject.External;
import io.github.eoinkanro.fakerest.core.conf.HttpHandlerConfigDeserializer;
import io.github.eoinkanro.fakerest.core.service.HandlerConfigService;
import io.javalin.http.Context;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
public abstract class AbstractApiHandler {

    protected final ObjectMapper objectMapper = new ObjectMapper();
    @External
    protected final HandlerConfigService handlerConfigService;
    @External
    protected final HttpHandlerConfigDeserializer handlerConfigDeserializer;

    public final void handle(Context context) {
        try {
            process(context);
            context.status(200);
        } catch (ApiHandlerException e) {
            context.status(e.getCode());
        } catch (Exception e) {
            context.status(500);
        }
    }

    protected abstract void process(Context context) throws Exception;

}
