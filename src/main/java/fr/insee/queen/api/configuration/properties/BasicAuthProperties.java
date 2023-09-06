package fr.insee.queen.api.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application.security.basic")
public record BasicAuthProperties(
        List<BasicUserProperties> users
) {}
