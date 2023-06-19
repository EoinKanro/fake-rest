package io.github.eoinkanro.fakerest.core.controller;

import io.github.eoinkanro.fakerest.core.model.RouterConfig;
import io.github.eoinkanro.fakerest.core.utils.HttpUtils;
import io.github.eoinkanro.fakerest.core.utils.RestClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Controller that can route requests
 */
@Slf4j
@AllArgsConstructor
public class RouterController implements BaseController {

    private static final String LOG_INFO = "Got router request \r\nMethod: [{}] \r\nUri: [{}]\r\nTo url:[{}] \r\nBody: [{}] \r\nHeaders: [{}]";

    private RouterConfig conf;
    private HttpUtils httpUtils;
    private RestClient restClient;

    @Override
    public ResponseEntity<String> handle(HttpServletRequest request) {
        ResponseEntity<String> result;
        try {
            HttpMethod method = HttpMethod.valueOf(conf.getMethod().name());
            URI uri = buildUri(request);
            String body = httpUtils.readBody(request);
            HttpHeaders headers = httpUtils.readHeaders(request);

            if (log.isTraceEnabled()) log.trace(LOG_INFO, request.getMethod(), request.getRequestURI(), uri, body, headers);

            result = restClient.execute(method, uri, headers, body);

        } catch (Exception e) {
            log.error("Error while redirecting from [{}] to [{}]", conf.getUri(), conf.getToUrl(), e);
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    /**
     * Create uri to route
     *
     * @param request - request to controller
     * @return - uri to route
     * @throws URISyntaxException - if something goes wrong with URI
     */
    private URI buildUri(HttpServletRequest request) throws URISyntaxException {
        URI result;
        if (conf.getToUrl().contains("://")) {
            result = new URI(conf.getToUrl());
        } else {
            String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            if (conf.getToUrl().charAt(0) == '/') {
                url = url + conf.getToUrl();
            } else {
                url = url + "/" + conf.getToUrl();
            }

            result = new URI(url);
        }
        return result;
    }

}
