package org.crumbs.core.context;

import org.crumbs.core.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigLoader {

    private static Logger LOGGER = Logger.getLogger(ConfigLoader.class);

    public static Map<String, String> loadProperties() {
        Properties properties = new Properties();
        InputStream propertiesStream = ConfigLoader.class.getClassLoader().getResourceAsStream("crumbs.properties");
        try {
            LOGGER.info("Found crumbs.properties, loading config");
            if (propertiesStream != null) {
                properties.load(propertiesStream);
                Map<String, String> propertiesMap = properties.keySet().stream()
                        .map(Object::toString)
                        .collect(Collectors.toMap(key -> key, properties::getProperty));
                Map<String, String> replaced = replaceValues(propertiesMap);
                replaced.putAll(System.getProperties().keySet().stream()
                        .map(Object::toString)
                        .collect(Collectors.toMap(key -> key, System::getProperty)
                ));
                return Collections.unmodifiableMap(replaced);
            }
        } catch (IOException e) {
            LOGGER.warn("No crumbs properties found in classpath");
        }
        return null;
    }

    private static Map<String, String> replaceValues(Map<String, String> propertyMap) {
        Map<String, String> replaced = propertyMap.keySet().stream()
                .collect(Collectors.toMap(key -> key, key -> System.getProperty(key, propertyMap.get(key))));
        return replaced.keySet()
                .stream()
                .collect(Collectors.toMap(key -> key, propertyKey -> {
                    String propertyValue = replaced.get(propertyKey);
                    if (propertyValue.contains("${") &&
                            propertyValue.substring(propertyValue.indexOf("${")).contains("}")) {
                        int start = propertyValue.indexOf("${");
                        int end = propertyValue.indexOf("}");
                        String prefix = propertyValue.substring(0, start);
                        String ref = propertyValue.substring(start + 2, end);
                        String suffix = propertyValue.substring(end + 1);
                        propertyValue = prefix + replaced.get(ref) + suffix;
                    }
                    return propertyValue;
                }));
    }
}
