package io.github.eoinkanro.fakerest.core.model;

import io.github.eoinkanro.fakerest.core.controller.GroovyController;
import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * Answer that returned groovy script in {@link GroovyController}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroovyAnswer {

    private HttpStatus httpStatus;

    private String answer;
}
