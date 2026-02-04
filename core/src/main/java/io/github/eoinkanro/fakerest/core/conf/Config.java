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
    private int mockPort = 8081;

    @Builder.Default
    private int uiPort = 8080;

    private Set<AbstractHttpHandlerConfig> handlers;

}
