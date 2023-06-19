package io.github.eoinkanro.fakerest.core.model;

import io.github.eoinkanro.fakerest.core.controller.BaseController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;
import java.util.Map;

/**
 * Config wrapper with additional information
 *
 * @param <T> - config class
 */
@Getter
@AllArgsConstructor
public class UriConfigHolder<T extends BaseUriConfig> {

    private T config;
    private Map<RequestMappingInfo, BaseController> requestMappingInfo;
    private List<String> usedUrls;

}
