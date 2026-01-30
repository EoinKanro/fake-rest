package io.github.eoinkanro.fakerest.core.handler;

import tools.jackson.core.TreeNode;

public interface HttpHandlerDataRegistry {

    TreeNode get(String key);

    void put(String key, TreeNode value);

}
