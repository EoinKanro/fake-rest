package io.github.eoinkanro.fakerest.ui.handler;

import io.javalin.http.Context;
import tools.jackson.databind.ObjectMapper;

public abstract class AbstractApiHandler {

    protected final ObjectMapper objectMapper = new ObjectMapper();

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
