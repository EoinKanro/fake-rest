package io.github.eoinkanro.fakerest.core.utils;

import io.github.eoinkanro.fakerest.core.model.ControllerResponse;
import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class RestClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public ControllerResponse execute(HttpMethod method, URI url, Map<String, List<String>> headers, String body) {
        ControllerResponse result;
        try {
            HttpRequest.BodyPublisher bodyPublisher = body == null ?
                    HttpRequest.BodyPublishers.noBody() :
                    HttpRequest.BodyPublishers.ofString(body);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(url)
                    .method(method.name(), bodyPublisher)
                    .headers(getHeaders(headers))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            result = ControllerResponse.builder()
                    .body(response.body())
                    .status(response.statusCode())
                    .build();

            result.getHeaders().putAll(response.headers().map());
        } catch (InterruptedException e) {
            if (Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
            }
            log.error("Interrupted while sending request", e);
            result = getBadResponse(e);
        } catch (Exception e) {
            result = getBadResponse(e);
        }
        return result;
    }

    private String[] getHeaders(Map<String, List<String>> headers) {
        int size = headers.values().stream()
                .mapToInt(List::size)
                .sum() * 2;

        String[] result = new String[size];
        int index = 0;

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String header = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                result[index++] = header;
                result[index++] = value;
            }
        }
        return result;
    }

    private ControllerResponse getBadResponse(Exception e) {
        return ControllerResponse.builder()
                .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                .body(e.getMessage())
                .build();
    }

}
