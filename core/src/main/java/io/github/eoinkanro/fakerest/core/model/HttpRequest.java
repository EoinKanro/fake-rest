package io.github.eoinkanro.fakerest.core.model;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class HttpRequest {

    private String body;

    private final Map<String, String> variables = new HashMap<>();

}
