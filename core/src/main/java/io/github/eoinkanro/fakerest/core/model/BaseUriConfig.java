package io.github.eoinkanro.fakerest.core.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Base config for controllers and routers
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class BaseUriConfig {

    private String id;

    private String uri;

    private RequestMethod method;
}
