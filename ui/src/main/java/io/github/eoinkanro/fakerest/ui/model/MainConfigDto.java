package io.github.eoinkanro.fakerest.ui.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainConfigDto {

    @Builder.Default
    private int mockPort = 8081;

    @Builder.Default
    private int uiPort = 8080;

}
