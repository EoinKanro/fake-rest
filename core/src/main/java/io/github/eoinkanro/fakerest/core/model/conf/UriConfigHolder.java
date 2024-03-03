package io.github.eoinkanro.fakerest.core.model.conf;

import io.github.eoinkanro.fakerest.core.controller.BaseController;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private Map<BaseUriConfig, BaseController> controllers;
    private List<String> usedUrls;

}
