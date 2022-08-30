package io.github.ivanrosw.fakerest.core.model;

import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * Answer that returned groovy script in {@link io.github.ivanrosw.fakerest.core.controller.GroovyController}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroovyAnswer {

    private HttpStatus httpStatus;

    private String answer;
}
