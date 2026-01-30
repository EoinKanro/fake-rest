package io.github.eoinkanro.fakerest.core.handler.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.github.eoinkanro.fakerest.core.conf.impl.GroovyHttpHandlerConfig;
import io.github.eoinkanro.fakerest.core.handler.HttpHandler;
import io.github.eoinkanro.fakerest.core.model.HttpRequest;
import io.github.eoinkanro.fakerest.core.model.HttpResponse;
import lombok.Getter;

public class GroovyHttpHandler implements HttpHandler {

    private static final String DEFAULT_GROOVY_IMPORT = """
                                                        import io.github.eoinkanro.fakerest.core.model.HttpResponse
                                                        import io.github.eoinkanro.fakerest.core.model.HttpRequest
                                                        """;

    @Getter
    private final GroovyHttpHandlerConfig config;
    private final GroovyShell groovyShell;
    private final String script;

    public GroovyHttpHandler(GroovyHttpHandlerConfig config) {
        this.config = config;

        Binding groovyBinding = new Binding();
        this.groovyShell = new GroovyShell(groovyBinding);
        this.script = DEFAULT_GROOVY_IMPORT + "\r\n" + config.getGroovyCode();
    }

    @Override
    public HttpResponse process(HttpRequest request) {
        groovyShell.setVariable("request", request);
        return (HttpResponse) groovyShell.evaluate(script);
    }

}
