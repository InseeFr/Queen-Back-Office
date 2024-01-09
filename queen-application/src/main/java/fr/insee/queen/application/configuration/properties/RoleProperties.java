package fr.insee.queen.application.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.roles")
public record RoleProperties(
        String interviewer,
        String reviewer,
        String admin,
        String webclient,
        String reviewerAlternative
) {
}
