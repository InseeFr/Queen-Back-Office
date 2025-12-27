package fr.insee.queen.application.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feature.synchronisation")
public record SynchronisationProperties(
        boolean enabled,
        String queenUrl) {
}