package io.github.eoinkanro.fakerest.core.model.conf;

import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Base config for controllers and routers
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseUriConfig {

    private String id;
    private String uri;
    private HttpMethod method;

}
