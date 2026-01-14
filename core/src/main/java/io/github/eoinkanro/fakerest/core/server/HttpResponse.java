package io.github.eoinkanro.fakerest.core.server;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HttpResponse {

    private int status;
    private String body;

}
