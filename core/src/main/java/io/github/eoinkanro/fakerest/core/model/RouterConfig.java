package io.github.eoinkanro.fakerest.core.model;

import lombok.*;

/**
 * Config for routers
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RouterConfig extends BaseUriConfig implements Copyable<RouterConfig> {

    private String toUrl;

    @Override
    public RouterConfig copy() {
        RouterConfig copy = new RouterConfig();
        copy.setId(this.getId());
        copy.setUri(this.getUri());
        copy.setMethod(this.getMethod());
        copy.setToUrl(toUrl);
        return copy;
    }
}
