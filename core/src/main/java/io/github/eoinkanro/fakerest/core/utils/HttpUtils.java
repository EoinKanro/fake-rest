package io.github.eoinkanro.fakerest.core.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtils {

    private static final String URL_ID_PATTERN = "(?<=\\{)[\\w]*(?=\\})";

    public static Map<String, String> getUrlIds(HttpServletRequest request) {
        return (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
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

    public static String readBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    }

    public static HttpHeaders readHeaders(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();

        Enumeration<String> headersNames = request.getHeaderNames();
        if (headersNames != null) {
            while (headersNames.hasMoreElements()) {
                String headerName = headersNames.nextElement();

                Enumeration<String> headerValues = request.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    String headerValue = headerValues.nextElement();
                    httpHeaders.add(headerName, headerValue);
                }
            }
        }

        return httpHeaders;
    }
}
