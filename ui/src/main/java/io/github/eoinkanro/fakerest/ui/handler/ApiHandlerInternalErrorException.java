package io.github.eoinkanro.fakerest.ui.handler;

public class ApiHandlerInternalErrorException extends ApiHandlerException {

    public ApiHandlerInternalErrorException() {
        super(500);
    }

}
