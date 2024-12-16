package fr.insee.queen.infrastructure.db.configuration;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "feature.sensitive-data")
@Validated
@Data
public class CipherProperties {
    private final boolean enabled;
    private final String encryptionSecretKey;

    public static final String ENCRYPTION_KEY_VALIDATION_ERROR_MESSAGE = "encryptionSecretKey must not be blank when sensitive-data is enabled";

    @AssertTrue(message = ENCRYPTION_KEY_VALIDATION_ERROR_MESSAGE)
    public boolean isEncryptionSecretKeyValid() {
        if (enabled) {
            return encryptionSecretKey != null && !encryptionSecretKey.isBlank();
        }
        return true;
    }
}
