package io.github.eoinkanro.fakerest.ui.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiHandlerException extends Exception {

    private final int code;

}
