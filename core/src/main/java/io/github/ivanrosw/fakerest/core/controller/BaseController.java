package io.github.ivanrosw.fakerest.core.controller;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

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
