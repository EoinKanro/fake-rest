package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.model.conf.ControllerConfig;
import io.github.eoinkanro.fakerest.core.conf.server.controller.ControllerData;
import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.github.eoinkanro.fakerest.core.utils.HttpUtils;
import io.undertow.server.HttpServerExchange;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpHeaders;

@Slf4j
public class GroovyController extends FakeController {

    private static final String LOG_INFO = "Got request \r\nMethod: [{}] \r\nUri: [{}] \r\nBody: [{}]";

    private static final String DEFAULT_GROOVY_IMPORT = """
                                                        import io.github.eoinkanro.fakerest.core.model.ControllerResponse
                                                        import jakarta.servlet.http.HttpServletResponse
                                                        import io.undertow.server.HttpServerExchange
                                                        import io.github.eoinkanro.commons.utils.JsonUtils
                                                        import io.github.eoinkanro.commons.utils.SystemUtils
                                                        import io.github.eoinkanro.fakerest.core.utils.HttpUtils
                                                        import io.github.eoinkanro.fakerest.core.conf.server.controller.ControllerData
                                                        import java.net.http.HttpHeaders
                                                        import com.fasterxml.jackson.databind.node.ObjectNode
                                                        """;

    private final GroovyShell groovyShell;

    @Builder
    public GroovyController(ControllerConfig controllerConfig, ControllerData controllerData) {
        Binding groovyBinding = new Binding();
        groovyShell = new GroovyShell(groovyBinding);
        groovyShell.setVariable("uri", controllerConfig.getUri());
        groovyShell.setVariable("controllerData", controllerData);
        this.controllerConfig = controllerConfig;
    }

    @Override
    public ControllerResponse handle(HttpServerExchange request) {
        delay();

        try {
            String body = HttpUtils.readBody(request);
            log.trace(LOG_INFO, request.getRequestMethod(), request.getRequestURI(), body);

            groovyShell.setVariable("body", body);
            HttpHeaders headers = HttpHeaders.of(HttpUtils.readHeaders(request), (s, s2) -> true);
            groovyShell.setVariable("headers", headers);

            ControllerResponse groovyAnswer = (ControllerResponse) groovyShell.evaluate(DEFAULT_GROOVY_IMPORT +
                    controllerConfig.getGroovyScript());

            return processGroovyAnswer(groovyAnswer);
        } catch (Exception e) {
            log.error("Controller: something went wrong", e);
            ObjectNode answer = JsonUtils.createJson();
            JsonUtils.putString(answer, DESCRIPTION_PARAM, e.getMessage());
            return ControllerResponse.builder()
                    .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(answer.toString())
                    .build();
        }
    }

    private ControllerResponse processGroovyAnswer(ControllerResponse groovyAnswer) {
        if (groovyAnswer.getStatus() <= 0) {
            groovyAnswer.setStatus(HttpServletResponse.SC_OK);
        }
        return groovyAnswer;
    }


}
