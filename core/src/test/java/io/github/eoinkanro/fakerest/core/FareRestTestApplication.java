package io.github.eoinkanro.fakerest.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

<<<<<<<< HEAD:core/src/main/java/io/github/eoinkanro/fakerest/core/FakeRestApplication.java
@SpringBootApplication(scanBasePackages = "io.github.eoinkanro.fakerest")
public class FakeRestApplication {
========
@SpringBootApplication(scanBasePackages = "io.github.ivanrosw.fakerest")
public class FareRestTestApplication {
>>>>>>>> 1f24281 (Create swing ui):core/src/test/java/io/github/eoinkanro/fakerest/core/FareRestTestApplication.java

    public static void main(String[] args) {
        SpringApplication.run(FareRestTestApplication.class, args);
    }

}
