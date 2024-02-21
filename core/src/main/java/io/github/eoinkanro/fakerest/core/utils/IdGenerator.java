package io.github.eoinkanro.fakerest.core.utils;

import io.github.eoinkanro.fakerest.core.model.GeneratorPattern;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private final AtomicInteger atomicInteger = new AtomicInteger();

    public String generateId(GeneratorPattern pattern) {
        String result;
        if (pattern == GeneratorPattern.SEQUENCE) {
            result = generateSequence();
        } else {
            result = generateUUID();
        }
        return result;
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String generateSequence() {
        return String.valueOf(atomicInteger.incrementAndGet());
    }
}
