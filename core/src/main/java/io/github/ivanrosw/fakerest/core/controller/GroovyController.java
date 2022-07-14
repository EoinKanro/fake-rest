package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.GroovyAnswer;
import io.github.ivanrosw.fakerest.core.utils.HttpUtils;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import io.github.ivanrosw.fakerest.core.utils.SystemUtils;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class GroovyController extends FakeController {

    private static final String DEFAULT_GROOVY_IMPORT = "import io.github.ivanrosw.fakerest.core.model.GroovyAnswer \n" +
                                                        "import org.springframework.http.HttpStatus \n" +
                                                        "import io.github.ivanrosw.fakerest.core.utils.JsonUtils \n" +
                                                        "import io.github.ivanrosw.fakerest.core.model.ControllerData \n" +
                                                        "import org.springframework.http.HttpHeaders \n";

    private final GroovyShell groovyShell;

    @Builder
    public GroovyController(ControllerData controllerData, ControllerConfig controllerConfig, JsonUtils jsonUtils, HttpUtils httpUtils, SystemUtils systemUtils) {
        Binding groovyBinding = new Binding();
        groovyShell = new GroovyShell(groovyBinding);
        groovyShell.setVariable("uri", controllerConfig.getUri());
        groovyShell.setVariable("controllerData", controllerData);
        groovyShell.setVariable("jsonUtils", jsonUtils);
        groovyShell.setVariable("systemUtils", systemUtils);
        this.controllerConfig = controllerConfig;
        this.jsonUtils = jsonUtils;
        this.httpUtils = httpUtils;
        this.systemUtils = systemUtils;
    }

    @Override
    public ResponseEntity<String> handle(HttpServletRequest request) {
        delay();

        try {
            String body = httpUtils.readBody(request);
            groovyShell.setVariable("body", body);
            HttpHeaders headers = httpUtils.readHeaders(request);
            groovyShell.setVariable("headers", headers);

            GroovyAnswer groovyAnswer = (GroovyAnswer) groovyShell.evaluate(DEFAULT_GROOVY_IMPORT +
                    controllerConfig.getGroovyScript());

            return processGroovyAnswer(groovyAnswer);
        } catch (Exception e) {
            log.error("Controller: something went wrong", e);
            ObjectNode answer = jsonUtils.createJson();
            jsonUtils.putString(answer, DESCRIPTION_PARAM, e.getMessage());
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
