package fr.insee.queen.infrastructure.db.surveyunit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feature.cipher")
public record CipherProperties(boolean enabled) {
}
