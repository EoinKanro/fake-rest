package io.github.eoinkanro.fakerest.ui.handler;

import io.github.eoinkanro.fakerest.core.service.HandlerConfigService;
import io.javalin.http.Context;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class DeleteHttpHandlerApiHandler extends AbstractApiHandler {

    private final HandlerConfigService handlerConfigService;

    @Override
    protected void process(Context context) throws Exception {
        String id = context.pathParam("id");
        if (id == null || id.isBlank()) {
            throw new ApiHandlerBadRequestException();
        }

        if (!handlerConfigService.deleteHandler(id)) {
            throw new ApiHandlerInternalErrorException();
        }
    }
}
