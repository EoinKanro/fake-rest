package io.github.eoinkanro.fakerest.core.conf;

import io.github.eoinkanro.fakerest.core.utils.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

abstract class MappingConfiguratorTest {

  protected static final String TEST_STATIC_URI = "/test";
  protected static final String TEST_STATIC_URI_SLASH_IN_END = "/test/";
  protected static final String TEST_COLLECTION_URI = "/test/{id}";

  protected static final String STATIC_ANSWER = "answer";
  protected static final String COLLECTION_JSON_ANSWER = "{\"id\":\"1\",\"data\":\"value\"}";
  protected static final String COLLECTION_ARRAY_JSON_ANSWER = "[{\"id\":\"1\",\"data\":\"value\"},{\"id\":\"2\",\"data\":\"value\"}]";
  protected static final String ID = "id";

  @MockBean
  private MappingConfigurationLoader mappingConfigurationLoader;
  @MockBean
  protected YamlConfigurator yamlConfigurator;

  @Autowired
  @Qualifier("requestMappingHandlerMapping")
  protected RequestMappingHandlerMapping handlerMapping;
  @Autowired
  protected MappingConfiguratorData mappingConfiguratorData;
  @Autowired
  protected JsonUtils jsonUtils;

  protected static String[] allUris;

  static {
    allUris = new String[]{TEST_STATIC_URI, TEST_COLLECTION_URI};
  }

  protected static Stream<RequestMethod> provideAllMethods() {
    return Stream.of(RequestMethod.values());
  }

  protected static Stream<String> provideAllUris() {
    return Stream.of(TEST_STATIC_URI, TEST_COLLECTION_URI);
  }

  protected static Stream<Arguments> provideAllUrisMethods() {
    List<Arguments> arguments = new ArrayList<>();
    RequestMethod[] requestMethods = RequestMethod.values();
    for (RequestMethod requestMethod : requestMethods) {
      for (String uri : allUris) {
        arguments.add(Arguments.of(uri, requestMethod));
      }
    }
    return Stream.of(arguments.toArray(new Arguments[0]));
  }

}
