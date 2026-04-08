package io.github.eoinkanro.fakerest.ui.handler;

public class ApiHandlerBadRequestException extends ApiHandlerException {

    public ApiHandlerBadRequestException() {
        super(400);
    }

}
