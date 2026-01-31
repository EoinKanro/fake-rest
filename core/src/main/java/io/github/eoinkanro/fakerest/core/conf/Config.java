package io.github.eoinkanro.fakerest.core.conf;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Config {

    @Builder.Default
    private int port = 8081;

    private Set<AbstractHttpHandlerConfig> handlers;

}
