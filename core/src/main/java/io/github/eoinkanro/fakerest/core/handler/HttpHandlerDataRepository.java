package io.github.eoinkanro.fakerest.core.handler;

import tools.jackson.core.TreeNode;

public interface HttpHandlerDataRepository {

    TreeNode get(String key);

    void put(String key, TreeNode value);

}
