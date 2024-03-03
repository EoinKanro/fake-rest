package io.github.eoinkanro.fakerest.core.controller;

import io.github.eoinkanro.commons.utils.SystemUtils;
import io.github.eoinkanro.fakerest.core.conf.server.controller.ControllerData;
import io.github.eoinkanro.fakerest.core.model.enums.ControllerSaveInfoMode;
import io.github.eoinkanro.fakerest.core.model.conf.ControllerConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for all CRUD and Groovy controllers
 */
@Slf4j
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class FakeController implements BaseController {

    protected static final String KEY_ALREADY_EXIST = "key [%s] already exist";
    protected static final String DATA_NOT_JSON = "data [%s] is not json";
    protected static final String KEY_NOT_FOUND = "key [%s] not found";

    protected static final String DESCRIPTION_PARAM = "description";

    protected ControllerSaveInfoMode saveInfoMode;
    protected ControllerData controllerData;
    protected ControllerConfig controllerConfig;

    protected void delay() {
        if (controllerConfig.getDelayMs() > 0) {
            SystemUtils.sleep(controllerConfig.getDelayMs());
        }
    }

}
