package io.github.eoinkanro.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.model.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.ControllerData;
import io.github.eoinkanro.fakerest.core.model.GroovyAnswer;
import io.github.eoinkanro.fakerest.core.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class GroovyController extends FakeController {

    private static final String LOG_INFO = "Got request \r\nMethod: [{}] \r\nUri: [{}] \r\nBody: [{}]";

    private static final String DEFAULT_GROOVY_IMPORT = """
                                                        import io.github.ivanrosw.fakerest.core.model.GroovyAnswer
                                                        import org.springframework.http.HttpStatus
                                                        import io.github.ivanrosw.fakerest.core.utils.JsonUtils
                                                        import io.github.ivanrosw.fakerest.core.utils.HttpUtils
                                                        import io.github.ivanrosw.fakerest.core.utils.SystemUtils
                                                        import io.github.ivanrosw.fakerest.core.model.ControllerData
                                                        import org.springframework.http.HttpHeaders
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
    public ResponseEntity<String> handle(HttpServletRequest request) {
        delay();

        try {
            String body = HttpUtils.readBody(request);
            if (log.isTraceEnabled()) log.trace(LOG_INFO, request.getMethod(), request.getRequestURI(), body);

            groovyShell.setVariable("body", body);
            HttpHeaders headers = HttpUtils.readHeaders(request);
            groovyShell.setVariable("headers", headers);

            GroovyAnswer groovyAnswer = (GroovyAnswer) groovyShell.evaluate(DEFAULT_GROOVY_IMPORT +
                    controllerConfig.getGroovyScript());

            return processGroovyAnswer(groovyAnswer);
        } catch (Exception e) {
            log.error("Controller: something went wrong", e);
            ObjectNode answer = JsonUtils.createJson();
            JsonUtils.putString(answer, DESCRIPTION_PARAM, e.getMessage());
            return new ResponseEntity<>(answer.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> processGroovyAnswer(GroovyAnswer groovyAnswer) {
        if (groovyAnswer.getHttpStatus() == null) {
            groovyAnswer.setHttpStatus(HttpStatus.OK);
        }
        return new ResponseEntity<>(groovyAnswer.getAnswer(), groovyAnswer.getHttpStatus());
    }


}
