package fr.insee.queen.application.configuration.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CrossEnvironmentCommunicationPropertiesTest {

    @Test
    @DisplayName("Properties should be correctly initialized when endpoint is true")
    void testPropertiesWhenEndpointIsTrue() {
        CrossEnvironmentCommunicationProperties properties = new CrossEnvironmentCommunicationProperties(true);
        assertThat(properties.endpoint()).isTrue();
    }

    @Test
    @DisplayName("Properties should be correctly initialized when endpoint is false")
    void testPropertiesWhenEndpointIsFalse() {
        CrossEnvironmentCommunicationProperties properties = new CrossEnvironmentCommunicationProperties(false);
        assertThat(properties.endpoint()).isFalse();
    }
}