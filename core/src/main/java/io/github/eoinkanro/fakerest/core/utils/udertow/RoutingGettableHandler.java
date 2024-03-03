package io.github.eoinkanro.fakerest.core.utils.udertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.util.HttpString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoutingGettableHandler extends RoutingHandler {

    private final Map<String, List<String>> registeredUris = new ConcurrentHashMap<>();

    @Override
    public synchronized RoutingHandler add(final String method, final String template, HttpHandler handler) {
        getUris(method).add(template);
        return super.add(method, template, handler);
    }

    @Override
    public RoutingHandler remove(HttpString method, String path) {
        getUris(method.toString()).remove(path);
        return super.remove(method, path);
    }

    private List<String> getUris(String method) {
        return registeredUris.computeIfAbsent(method, k -> new ArrayList<>());
    }

    public boolean hasUri(String method, String uri) {
        return getUris(method).stream().anyMatch(k -> k.equals(uri));
    }

}
