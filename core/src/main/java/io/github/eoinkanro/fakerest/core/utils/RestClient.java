package io.github.eoinkanro.fakerest.core.utils;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class RestClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> execute(HttpMethod method, URI url, HttpHeaders headers, String body) {
        ResponseEntity<String> result;
        try {
            RequestEntity<String> requestEntity = RequestEntity
                    .method(method, url)
                    .headers(headers)
                    .body(body);
            result = restTemplate.exchange(requestEntity, String.class);
        } catch (HttpStatusCodeException e) {
            result = ResponseEntity.status(e.getStatusCode().value())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            result = ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        } catch (Exception e) {
            result = ResponseEntity.status(0).body(e.getMessage());
        }
        return result;
    }

}
