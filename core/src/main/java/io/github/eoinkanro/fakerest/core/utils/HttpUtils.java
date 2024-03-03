package io.github.eoinkanro.fakerest.core.utils;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.PathTemplateMatch;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtils {

    private static final String URL_ID_PATTERN = "(?<=\\{)[\\w]*(?=\\})";
    public static final String URI_DELIMITER = "/";

    public static Map<String, String> getUrlIds(HttpServerExchange request, List<String> idNames) {
        Map<String, String> result = new HashMap<>();
        PathTemplateMatch pathMatch = request.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        idNames.forEach(id -> result.put(id, pathMatch.getParameters().get(id)));
        return result;
    }

    public static List<String> getIdParams(String url) {
        Pattern pattern = Pattern.compile(URL_ID_PATTERN);
        Matcher matcher = pattern.matcher(url);

        List<String> idParams = new ArrayList<>();
        while (matcher.find()) {
            idParams.add(matcher.group());
        }

        return idParams;
    }

    public static String getBaseUri(String uri) {
        return uri.substring(0, uri.indexOf("{"));
    }

    public static String readBody(HttpServerExchange request) throws IOException {
        var inputStream = request.getInputStream();
        if (inputStream == null) {
            return null;
        }

        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        return requestBody.toString();
    }

    public static Map<String, List<String>> readHeaders(HttpServerExchange request) {
        Map<String, List<String>> httpHeaders = new HashMap<>();

        Iterable<HeaderValues> headersNames = request.getRequestHeaders();
        if (headersNames != null) {
            for (HeaderValues headerValues : headersNames) {
                String header = headerValues.getHeaderName().toString();

                headerValues.forEach(value -> {
                    List<String> values = httpHeaders.computeIfAbsent(header, key -> new ArrayList<>());
                    values.add(value);
                });
            }
        }

        return httpHeaders;
    }
}
