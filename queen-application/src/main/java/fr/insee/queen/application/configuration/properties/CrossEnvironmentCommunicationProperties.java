package fr.insee.queen.application.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feature.cross-environnement-communication")
public record CrossEnvironmentCommunicationProperties(
        boolean endpoint) {
}