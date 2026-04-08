package io.github.eoinkanro.fakerest.ui.handler;

import io.github.eoinkanro.fakerest.core.conf.HttpHandlerConfigDeserializer;
import io.github.eoinkanro.fakerest.core.service.HandlerConfigService;
import io.javalin.http.Context;
import jakarta.inject.Singleton;

import static io.github.eoinkanro.fakerest.ui.server.UiServer.ID_PATH;

@Singleton
public class DeleteHttpHandlerApiHandler extends AbstractApiHandler {

    public DeleteHttpHandlerApiHandler(HandlerConfigService handlerConfigService, HttpHandlerConfigDeserializer handlerConfigDeserializer) {
        super(handlerConfigService, handlerConfigDeserializer);
    }

    @Override
    protected void process(Context context) throws Exception {
        String id = context.pathParam(ID_PATH);
        if (id == null || id.isBlank()) {
            throw new ApiHandlerBadRequestException();
        }

        if (!handlerConfigService.deleteHandler(id)) {
            throw new ApiHandlerInternalErrorException();
        }
    }
}
