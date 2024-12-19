package fr.insee.queen.infrastructure.db.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CipherPropertiesTest {

    @Test
    @DisplayName("Test that encryptionSecretKey validation passes when sensitive-data is disabled")
    void testEncryptionSecretKeyValidationWhenCipherDisabled() {
        // given
        CipherProperties properties = new CipherProperties(false, null);

        // when
        boolean isValid = properties.isEncryptionSecretKeyValid();

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Test that encryptionSecretKey validation fails when sensitive-data is enabled and key is null")
    void testEncryptionSecretKeyValidationFailsWhenCipherEnabledAndKeyIsNull() {
        // given
        CipherProperties properties = new CipherProperties(true, null);

        // when & then
        assertThat(properties.isEncryptionSecretKeyValid()).isFalse();
    }

    @Test
    @DisplayName("Test that encryptionSecretKey validation fails when sensitive-data is enabled and key is blank")
    void testEncryptionSecretKeyValidationFailsWhenCipherEnabledAndKeyIsBlank() {
        // given
        CipherProperties properties = new CipherProperties(true, "   ");

        // when & then
        assertThat(properties.isEncryptionSecretKeyValid()).isFalse();
    }

    @Test
    @DisplayName("Test that encryptionSecretKey validation passes when sensitive-data is enabled and key is valid")
    void testEncryptionSecretKeyValidationPassesWhenCipherEnabledAndKeyIsValid() {
        // given
        CipherProperties properties = new CipherProperties(true, "my-secret-key");

        // when
        boolean isValid = properties.isEncryptionSecretKeyValid();

        // then
        assertThat(isValid).isTrue();
    }
}