package org.crumbs.core.context;

import org.crumbs.core.exception.CrumbsInitException;
import org.crumbs.core.logging.Logger;
import org.crumbs.core.annotation.Crumb;
import org.crumbs.core.annotation.CrumbInit;
import org.crumbs.core.annotation.CrumbRef;
import org.crumbs.core.annotation.Property;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class CrumbsContext {

    private static final Logger LOGGER = Logger.getLogger(CrumbsContext.class);

    private final Map<Class<?>, Object> crumbs = new HashMap<>();
    private Properties properties;

    void initialize(Class<?> clazz) throws Exception {
        LOGGER.info("Starting Crumbs Context ...");
        long start = System.currentTimeMillis();
        loadProperties();
        loadCrumbs("org.crumbs");
        loadCrumbs(clazz);
        injectReferences();
        if (properties != null) {
            injectProperties();
        }
        initCrumbs();
        LOGGER.info("Crumbs context initialized in {} millis", System.currentTimeMillis() - start);
    }

    private void initCrumbs() {
        crumbs.values().forEach(crumb -> Arrays.stream(crumb.getClass().getDeclaredMethods()).forEach(method -> {
            Arrays.stream(method.getAnnotations())
                    .filter(annotation -> annotation.annotationType().equals(CrumbInit.class))
                    .findFirst()
                    .ifPresent(ann -> {
                        method.setAccessible(true);
                        try {
                            method.invoke(crumb);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new CrumbsInitException("Failed to call init methods on crumb "
                                    + crumb.getClass().getCanonicalName(), e);
                        }
                    });
        }));
    }

    private void injectProperties() {
        crumbs.values().forEach(crumb -> Arrays.stream(crumb.getClass().getDeclaredFields()).forEach(field -> {
            Arrays.stream(field.getAnnotations())
                    .filter(annotation -> annotation.annotationType().equals(Property.class))
                    .findFirst()
                    .ifPresent(annotation -> {
                        field.setAccessible(true);
                        Property property = field.getAnnotation(Property.class);
                        String propertyKey = property.value();
                        String value = properties.getProperty(propertyKey);
                        try {
                            Class type = field.getType();
                            if (type.equals(String.class)) {
                                field.set(crumb, value);
                            } else if (type.equals(Integer.class)) {
                                Integer intValue = value == null ? null : Integer.parseInt(value);
                                field.set(crumb, intValue);
                            } else if (type.equals(Long.class)) {
                                Long longValue = value == null ? null : Long.parseLong(value);
                                field.set(crumb, longValue);
                            } else if (type.equals(Double.class)) {
                                Double doubleValue = value == null ? null : Double.parseDouble(value);
                                field.set(crumb, doubleValue);
                            } else if (type.equals(Boolean.class)) {
                                Boolean boolValue = value == null ? null : Boolean.parseBoolean(value);
                                field.set(crumb, boolValue);
                            } else if (type.equals(Duration.class)) {
                                Duration duration = value == null ? null : Duration.parse(value);
                                field.set(crumb, duration);
                            } else {
                                throw new CrumbsInitException("Could not inject value in field " + field.getName() +
                                        " of type " + type.getCanonicalName() + " in class "
                                        + crumb.getClass().getCanonicalName() + ". Unsupported property type");
                            }
                            field.setAccessible(false);
                        } catch (IllegalAccessException e) {
                            throw new CrumbsInitException("Unable to set field value due to exception", e);
                        }
                    });
        }));
    }

    public <T> T getCrumb(Class<T> clazz) {
        return (T) crumbs.get(clazz);
    }

    private void injectReferences() {
        crumbs.values().forEach(crumb -> {
            Arrays.stream(crumb.getClass().getDeclaredFields()).forEach(field -> {
                if (Arrays.stream(field.getAnnotations())
                        .anyMatch(annotation -> annotation.annotationType().equals(CrumbRef.class))) {
                    field.setAccessible(true);
                    try {
                        Object value = crumbs.get(field.getType());
                        if(value == null) {
                            throw new CrumbsInitException("Could not inject reference in object of type "
                                    + crumb.getClass().getCanonicalName() +
                                    ". No Crumbs of type " + field.getType().getCanonicalName() + " found. ");
                        }
                        field.set(crumb, value);
                        field.setAccessible(false);
                    } catch (IllegalAccessException e) {
                        throw new CrumbsInitException("Unable to set field value due to exception", e);
                    }
                }
            });
        });
    }

    private void loadCrumbs(Class<?> clazz) throws Exception {
        String packageName = clazz.getPackage().getName();
        loadCrumbs(packageName);
    }

    private void loadCrumbs(String packageName) throws Exception {
        Set<Class<?>> scannedCrumbs;
        try {
            scannedCrumbs = Scanner.getClassesInPackage(packageName).stream()
                    .filter(scannedClazz -> Arrays.stream(scannedClazz.getAnnotations())
                            .anyMatch(annotation -> annotation.annotationType().equals(Crumb.class)))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new CrumbsInitException("Error occurred on load classes", e);
        }

        scannedCrumbs.forEach(crumbClass -> {
            try {
                crumbs.put(crumbClass, crumbClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new CrumbsInitException("A fatal error occurred on class instantiation", e);
            }
        });
    }

    private void loadProperties() {
        this.properties = new Properties();
        InputStream propertiesStream = this.getClass().getClassLoader().getResourceAsStream("crumbs.properties");
        try {
            LOGGER.debug("Found crumbs.properties, loading config");
            if(propertiesStream != null) {
                properties.load(propertiesStream);
            }
        } catch (IOException e) {
            LOGGER.warn("No crumbs properties found in classpath");
        }
    }
}
