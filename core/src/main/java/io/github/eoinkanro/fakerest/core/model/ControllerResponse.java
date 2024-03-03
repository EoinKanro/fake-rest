package io.github.eoinkanro.fakerest.core.model;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControllerResponse {

    private int status;
    private String body;
    private final Map<String, List<String>> headers = new HashMap<>();

}
