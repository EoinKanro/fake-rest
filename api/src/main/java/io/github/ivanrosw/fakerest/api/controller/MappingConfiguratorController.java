package io.github.ivanrosw.fakerest.api.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.conf.ConfigException;
import io.github.ivanrosw.fakerest.core.conf.ControllerMappingConfigurator;
import io.github.ivanrosw.fakerest.core.conf.MappingConfiguratorData;
import io.github.ivanrosw.fakerest.core.conf.RouterMappingConfigurator;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.RouterConfig;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that can handle requests to create\delete controllers\routers configurations
 */
@Slf4j
@CrossOrigin("localhost")
@RestController
@RequestMapping("/api/conf/mapping")
public class MappingConfiguratorController {

    private static final String ERROR_DESCRIPTION = "description";

    @Autowired
    private RouterMappingConfigurator routersConfigurator;
    @Autowired
    private ControllerMappingConfigurator controllersConfigurator;
    @Autowired
    private MappingConfiguratorData configuratorData;

    @Autowired
    private JsonUtils jsonUtils;

    //CONTROLLER

    @GetMapping("/controller")
    public ResponseEntity<List<ControllerConfig>> getAllControllers() {
        return new ResponseEntity<>(configuratorData.getAllControllersCopy(), HttpStatus.OK);
    }

    @GetMapping("/controller/{id}")
    public ResponseEntity<ControllerConfig> getController(@PathVariable String id) {
        ControllerConfig controller = configuratorData.getControllerCopy(id);
        ResponseEntity<ControllerConfig> response;
        if (controller != null) {
            response = new ResponseEntity<>(controller, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @PostMapping("/controller")
    public ResponseEntity<String> addController(@RequestBody ControllerConfig conf) {
        log.info("Got request for create controller:");
        log.info(String.valueOf(conf));
        return updateConfig(new ControllerAdder(), conf);
    }
    private class ControllerAdder implements UpdateProcessor<ControllerConfig> {
        @Override
        public String process(ControllerConfig conf) throws ConfigException {
            controllersConfigurator.registerController(conf);
            return jsonUtils.toObjectNode(conf).toString();
        }
    }

    @DeleteMapping("/controller/{id}")
    public ResponseEntity<String> deleteController(@PathVariable String id) {
        return updateConfig(new ControllerDeleter(), id);
    }
    private class ControllerDeleter implements UpdateProcessor<String> {
        @Override
        public String process(String id) throws ConfigException {
            ControllerConfig conf = configuratorData.getControllerCopy(id);
            controllersConfigurator.unregisterController(id);
            return jsonUtils.toObjectNode(conf).toString();
        }
    }

    //ROUTER

    @GetMapping("/router")
    public ResponseEntity<List<RouterConfig>> getAllRouters() {
        return new ResponseEntity<>(configuratorData.getAllRoutersCopy(), HttpStatus.OK);
    }

    @GetMapping("/router/{id}")
    public ResponseEntity<RouterConfig> getRouter(@PathVariable String id) {
        RouterConfig router = configuratorData.getRouterCopy(id);
        ResponseEntity<RouterConfig> response;
        if (router != null) {
            response = new ResponseEntity<>(router, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @PostMapping("/router")
    public ResponseEntity<String> addRouter(@RequestBody RouterConfig conf) {
        log.info("Got request for create router:");
        log.info(String.valueOf(conf));
        return updateConfig(new RouterAdder(), conf);
    }
    private class RouterAdder implements UpdateProcessor<RouterConfig> {
        @Override
        public String process(RouterConfig conf) throws ConfigException {
            routersConfigurator.registerRouter(conf);
            return jsonUtils.toObjectNode(conf).toString();
        }
    }

    @DeleteMapping("/router/{id}")
    public ResponseEntity<String> deleteRouter(@PathVariable String id) {
        return updateConfig(new RouterDeleter(), id);
    }
    private class RouterDeleter implements UpdateProcessor<String> {
        @Override
        public String process(String id) throws ConfigException {
            RouterConfig conf = configuratorData.getRouterCopy(id);
            routersConfigurator.unregisterRouter(id);
            return jsonUtils.toObjectNode(conf).toString();
        }
    }

    //GENERAL

    /**
     * Base method to process request with all checks and exception handles
     *
     * @param updater - way of process
     * @param data - data to process
     * @return - response
     * @param <T> - class of data to process
     */
    private <T> ResponseEntity<String> updateConfig(UpdateProcessor<T> updater, T data) {
        ResponseEntity<String> response;
        ObjectNode body;
        if (data != null) {
            try {
                response = new ResponseEntity<>(updater.process(data), HttpStatus.OK);
            } catch (ConfigException e) {
                body = jsonUtils.createJson();
                jsonUtils.putString(body, ERROR_DESCRIPTION, e.getMessage());
                response = new ResponseEntity<>(body.toString(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                body = jsonUtils.createJson();
                jsonUtils.putString(body, ERROR_DESCRIPTION, e.getMessage());
                response = new ResponseEntity<>(body.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            body = jsonUtils.createJson();
            jsonUtils.putString(body, ERROR_DESCRIPTION, "Configuration is empty");
            response = new ResponseEntity<>(body.toString(), HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    /**
     * Class to describe way of process handled configuration
     *
     * @param <T> - class of data to process
     */
    private interface UpdateProcessor<T> {
        String process(T conf) throws ConfigException;
    }

}
