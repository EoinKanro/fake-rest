package io.github.eoinkanro.fakerest.core.controller;

import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.undertow.server.HttpServerExchange;

/**
 * Base interface for all controllers
 */
public interface BaseController {

    /**
     * Handle and process request
     *
     * @param request - request to controller
     * @return - response
     */
    ControllerResponse handle(HttpServerExchange request);
}
