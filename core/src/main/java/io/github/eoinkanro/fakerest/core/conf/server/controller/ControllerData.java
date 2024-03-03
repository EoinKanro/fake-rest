package io.github.eoinkanro.fakerest.core.conf.server.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.commons.utils.JsonUtils;
import io.github.eoinkanro.fakerest.core.model.enums.ControllerSaveInfoMode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean contains all data for Controllers with mode {@link ControllerSaveInfoMode#COLLECTION}
 * And methods to work with data
 */
public class ControllerData {

    private static final String KEY_DELIMITER = ":::";

    /**
     * Collection with url - controller data
     */
    private final Map<String, Map<String, ObjectNode>> allData = new ConcurrentHashMap<>();

    public Map<String, ObjectNode> getAllData(String url) {
        return getDataCollection(url);
    }

    public ObjectNode getData(String url, String key) {
        return getDataCollection(url).get(key);
    }

    public void putData(String url, String key, ObjectNode data) {
        getDataCollection(url).put(key, data);
    }

    public boolean containsKey(String url, String key) {
        return getDataCollection(url).containsKey(key);
    }

    public void deleteData(String url, String key) {
        getDataCollection(url).remove(key);
    }

    public void deleteAllData(String url) {
        allData.remove(url);
    }

    private Map<String, ObjectNode> getDataCollection(String url) {
        return allData.computeIfAbsent(url, key -> new ConcurrentHashMap<>());
    }

    public String buildKey(ObjectNode data, List<String> idParams) {
        List<String> ids = idParams.stream().map(param -> JsonUtils.getString(data, param)).toList();
        return String.join(KEY_DELIMITER, ids);
    }

    public String buildKey(Map<String, String> data, List<String> idParams) {
        List<String> ids = idParams.stream().map(data::get).toList();
        return String.join(KEY_DELIMITER, ids);
    }


}
