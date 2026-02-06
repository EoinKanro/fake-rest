package io.github.eoinkanro.fakerest.core.handler.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.TreeNode;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class HttpHandlerDataRepositoryImplTest {

    @Mock
    private TreeNode treeNode;

    @InjectMocks
    private HttpHandlerDataRepositoryImpl subject;

    @Test
    void test() {
        String key = UUID.randomUUID().toString();

        assertNull(subject.get(key));
        
        subject.put(key, treeNode);
        assertSame(treeNode, subject.get(key));
    }

}
