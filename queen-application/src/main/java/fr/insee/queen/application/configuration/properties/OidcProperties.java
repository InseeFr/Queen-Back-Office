package fr.insee.queen.application.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "application.security.oidc")
public record OidcProperties(
        String authServerHost,
        String authServerUrl,
        String realm,
        String principalAttribute,
        String roleClaim,
        String clientId) {
}
