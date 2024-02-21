package io.github.eoinkanro.fakerest.core.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Config for controllers
 */
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
public class ControllerConfig extends BaseUriConfig implements Copyable<ControllerConfig> {

    private ControllerFunctionMode functionMode;

    private String answer;

    private long delayMs;

    private List<String> idParams;

    private boolean generateId;

    private Map<String, GeneratorPattern> generateIdPatterns;

    private String groovyScript;

    public ControllerConfig() {
        idParams = new ArrayList<>();
        generateIdPatterns = new HashMap<>();
    }

    @Override
    public ControllerConfig copy() {
        ControllerConfig copy = new ControllerConfig();
        copy.setId(this.getId());
        copy.setUri(this.getUri());
        copy.setMethod(this.getMethod());
        copy.setFunctionMode(this.functionMode);
        copy.setAnswer(this.answer);
        copy.setDelayMs(this.delayMs);
        copy.setIdParams(new ArrayList<>(this.idParams));
        copy.setGenerateId(this.generateId);
        copy.setGenerateIdPatterns(new HashMap<>(this.generateIdPatterns));
        copy.setGroovyScript(this.groovyScript);
        return copy;
    }
}
