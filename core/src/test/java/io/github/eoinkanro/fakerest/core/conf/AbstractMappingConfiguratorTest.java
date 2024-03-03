package io.github.eoinkanro.fakerest.core.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import io.github.eoinkanro.fakerest.core.conf.server.UndertowServer;
import io.github.eoinkanro.fakerest.core.conf.file.YamlConfigurator;
import io.github.eoinkanro.fakerest.core.conf.server.MappingConfigurationsInfo;
import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.Mock;
import org.mockito.Spy;

abstract class AbstractMappingConfiguratorTest {

    protected static final String TEST_STATIC_URI = "/test";
    protected static final String TEST_STATIC_URI_SLASH_IN_END = "/test/";
    protected static final String TEST_COLLECTION_URI = "/test/{id}";

    protected static final String STATIC_ANSWER = "answer";
    protected static final String COLLECTION_JSON_ANSWER = "{\"id\":\"1\",\"data\":\"value\"}";
    protected static final String COLLECTION_ARRAY_JSON_ANSWER = "[{\"id\":\"1\",\"data\":\"value\"},{\"id\":\"2\",\"data\":\"value\"}]";
    protected static final String ID = "id";

    @Mock
    protected YamlConfigurator yamlConfigurator;

    @Spy
    protected UndertowServer server;
    @Spy
    protected MappingConfigurationsInfo mappingConfigurationsInfo;

    protected static final String[] allUris = new String[]{TEST_STATIC_URI, TEST_COLLECTION_URI};

    protected static Stream<HttpMethod> provideAllMethods() {
        return Stream.of(HttpMethod.values());
    }

    protected static Stream<String> provideAllUris() {
        return Stream.of(TEST_STATIC_URI, TEST_COLLECTION_URI);
    }

    protected static Stream<Arguments> provideAllUrisMethods() {
        List<Arguments> arguments = new ArrayList<>();
        HttpMethod[] requestMethods = HttpMethod.values();
        for (HttpMethod requestMethod : requestMethods) {
            for (String uri : allUris) {
                arguments.add(Arguments.of(uri, requestMethod));
            }
        }
        return Stream.of(arguments.toArray(new Arguments[0]));
    }

}
