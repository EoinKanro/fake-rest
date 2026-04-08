package io.github.eoinkanro.fakerest.core.handler.impl;

import io.github.eoinkanro.fakerest.core.handler.HttpHandlerDataRepository;
import jakarta.inject.Singleton;
import tools.jackson.core.TreeNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class HttpHandlerDataRepositoryImpl implements HttpHandlerDataRepository {

    private final Map<String, TreeNode> data = new ConcurrentHashMap<>();

    @Override
    public TreeNode get(String key) {
        return data.get(key);
    }

    @Override
    public void put(String key, TreeNode value) {
        data.put(key, value);
    }

}
