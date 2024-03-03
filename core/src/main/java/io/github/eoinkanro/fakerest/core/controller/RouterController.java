package io.github.eoinkanro.fakerest.core.controller;

import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import io.github.eoinkanro.fakerest.core.model.conf.RouterConfig;
import io.github.eoinkanro.fakerest.core.utils.HttpUtils;
import io.github.eoinkanro.fakerest.core.utils.RestClient;
import io.undertow.server.HttpServerExchange;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Controller that can route requests
 */
@Slf4j
@AllArgsConstructor
public class RouterController implements BaseController {

    private static final String LOG_INFO = "Got router request \r\nMethod: [{}] \r\nUri: [{}]\r\nTo url:[{}] \r\nBody: [{}] \r\nHeaders: [{}]";

    private RouterConfig conf;
    private RestClient restClient;

    @Override
    public ControllerResponse handle(HttpServerExchange request) {
        ControllerResponse result;
        try {
            HttpMethod method = conf.getMethod();
            URI uri = buildUri(request);
            String body = HttpUtils.readBody(request);
            Map<String, List<String>> headers = HttpUtils.readHeaders(request);

            log.trace(LOG_INFO, request.getRequestMethod(), request.getRequestURI(), uri, body, headers);

            result = restClient.execute(method, uri, headers, body);

        } catch (Exception e) {
            log.error("Error while redirecting from [{}] to [{}]", conf.getUri(), conf.getToUrl(), e);
            result = ControllerResponse.builder()
                    .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .build();
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
    private URI buildUri(HttpServerExchange request) throws URISyntaxException {
        URI result;
        if (conf.getToUrl().contains("://")) {
            result = new URI(conf.getToUrl());
        } else {
            String url = request.getRequestScheme() + "://" + request.getHostName() + ":" + request.getHostPort();
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
