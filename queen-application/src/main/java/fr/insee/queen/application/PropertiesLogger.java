package fr.insee.queen.application;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class PropertiesLogger implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Set<String> hiddenWords = Set.of("password", "pwd", "jeton", "token", "secret");
    private static final List<String> propertyPrefixes = List.of("application", "spring", "feature", "logging");

    @Override
    public void onApplicationEvent(@NonNull ApplicationEnvironmentPreparedEvent event) {
        Environment environment = event.getEnvironment();

        log.info("===============================================================================================");
        log.info("                                     Properties                                                ");

        getFilteredPropertyStream((AbstractEnvironment) environment)
                .forEach(key -> log.info("{} = {}", key, hideProperties(key.toLowerCase(), environment)));

        log.info("===============================================================================================");
    }

    /**
     * Retrieve filtered properties starting with propertyPrefixes and with masked values for hidden words
     * @param environment Spring environment
     * @return filtered properties
     */
    Stream<String> getFilteredPropertyStream(AbstractEnvironment environment) {
        return environment.getPropertySources().stream()
                .filter(EnumerablePropertySource.class::isInstance)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .filter(Objects::nonNull)
                .sorted()
                .filter(this::isRelevantProperty);
    }

    /**
     * Is the property relevant to be logged
     * @param property to check
     * @return true if relevant false otherwise
     */
    boolean isRelevantProperty(String property) {
        return propertyPrefixes
                .stream()
                .anyMatch(property::startsWith);
    }

    /**
     *
     * @param key key property
     * @param environment environment
     * @return property value
     */
    Object hideProperties(String key, Environment environment) {
        if (hiddenWords.stream().anyMatch(key::contains)) {
            return "******";
        }
        return environment.getProperty(key);
    }
}