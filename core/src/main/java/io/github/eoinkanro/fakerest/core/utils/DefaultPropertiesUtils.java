package io.github.eoinkanro.fakerest.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

//TODO
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultPropertiesUtils {

    private static final String PORT_PROPERTY = "port";

    private static final int DEFAULT_PORT = 8080;

    private static final String CONFIG_NAME = "application.yml";

    /**
     * @throws NumberFormatException if port parameter is not empty and is not an int
     * @return port of server
     */
    public static int getPort() {
        String port = System.getProperty(PORT_PROPERTY);
        if (port == null || port.isBlank()) {
            return DEFAULT_PORT;
        }
        return Integer.parseInt(port);
    }

    public static String getConfigFileName() {
        return CONFIG_NAME;
    }

}
