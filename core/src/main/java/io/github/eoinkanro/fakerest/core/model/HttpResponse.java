package io.github.eoinkanro.fakerest.core.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HttpResponse {

    private int code;
    private String body;

}
