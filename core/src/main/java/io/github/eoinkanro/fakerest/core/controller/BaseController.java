package io.github.eoinkanro.fakerest.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

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
    ResponseEntity<String> handle(HttpServletRequest request);
}
