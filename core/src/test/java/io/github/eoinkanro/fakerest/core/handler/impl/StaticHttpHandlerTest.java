package io.github.eoinkanro.fakerest.core.handler.impl;

import io.github.eoinkanro.fakerest.core.model.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class StaticHttpHandlerTest {

    @Test
    void testHandle() {
        HttpResponse response = HttpResponse.builder().build();
        StaticHttpHandler subject = new StaticHttpHandler(null, response);

        assertSame(response, subject.handle(null));
    }

}
