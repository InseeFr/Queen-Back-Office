package fr.insee.queen.infrastructure.db.surveyunit;

import jakarta.validation.constraints.AssertTrue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "feature.cipher")
@Validated
public record CipherProperties(
        boolean enabled,
        String encryptionSecretKey) {

    @AssertTrue(message = "encryptionSecretKey must not be blank when cipher is enabled")
    public boolean isEncryptionSecretKeyValid() {
        if (enabled) {
            return encryptionSecretKey != null && !encryptionSecretKey.isBlank();
        }
        return true;
    }
}
