package io.github.eoinkanro.fakerest.core.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class HttpResponse {

    private int code;
    private String body;

}
