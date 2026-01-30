package io.github.eoinkanro.fakerest.core.handler.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.github.eoinkanro.fakerest.core.conf.impl.GroovyHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.handler.HttpHandlerDataRegistry;
import io.github.eoinkanro.fakerest.core.model.HttpRequest;
import io.github.eoinkanro.fakerest.core.model.HttpResponse;
import lombok.Getter;
import tools.jackson.databind.json.JsonMapper;

public class GroovyHttpHandler implements HttpHandler {

    private static final String DEFAULT_GROOVY_IMPORT = """
                                                        import io.github.eoinkanro.fakerest.core.model.HttpResponse
                                                        import io.github.eoinkanro.fakerest.core.model.HttpRequest
                                                        import io.github.eoinkanro.fakerest.core.handler.HttpHandlerDataRegistry
                                                        import tools.jackson.databind.node.ObjectNode
                                                        import tools.jackson.databind.node.ArrayNode
                                                        import tools.jackson.databind.json.JsonMapper
                """;

    @Getter
    private final GroovyHttpHandlerConfig config;
    private final GroovyShell groovyShell;
    private final String script;

    public GroovyHttpHandler(GroovyHttpHandlerConfig config, HttpHandlerDataRegistry dataRegistry) {
        this.config = config;

        JsonMapper jsonMapper = JsonMapper.builder().build();

        Binding groovyBinding = new Binding();
        this.groovyShell = new GroovyShell(groovyBinding);
        this.groovyShell.setVariable("dataRegistry", dataRegistry);
        this.groovyShell.setVariable("jsonMapper", jsonMapper);

        this.script = DEFAULT_GROOVY_IMPORT + "\r\n" + config.getGroovyCode();
    }

    @Override
    public HttpResponse process(HttpRequest request) {
        groovyShell.setVariable("request", request);
        return (HttpResponse) groovyShell.evaluate(script);
    }

}
