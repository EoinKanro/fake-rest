package io.github.ivanrosw.fakerest.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Class with system utils
 * It's exist mostly for coverage... :c
 */

@Slf4j
@Component
public class SystemUtils {

    public void sleep(long sleepMs) {
        try {
            Thread.sleep(sleepMs);
        } catch (Exception e) {
            log.error("Interrupt error", e);
            Thread.currentThread().interrupt();
        }
    }
}
