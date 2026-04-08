package io.github.eoinkanro.fakerest.ui.handler;

public class ApiHandlerNotFoundException extends ApiHandlerException {

    public ApiHandlerNotFoundException() {
        super(404);
    }

}
