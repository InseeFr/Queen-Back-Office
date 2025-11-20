package fr.insee.queen.application.crossenvironmentcommunication.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CrossEnvironmentCommunicationPropertiesTest {

    @Test
    @DisplayName("Should create properties with emitter enabled and consumer disabled")
    void testPropertiesCreationWithEmitterEnabledConsumerDisabled() {
        // given
        boolean emitter = true;
        boolean consumer = false;

        // when
        CrossEnvironmentCommunicationProperties properties =
            new CrossEnvironmentCommunicationProperties(emitter, consumer);

        // then
        assertThat(properties.emitter()).isTrue();
        assertThat(properties.consumer()).isFalse();
    }

    @Test
    @DisplayName("Should create properties with emitter disabled and consumer enabled")
    void testPropertiesCreationWithEmitterDisabledConsumerEnabled() {
        // given
        boolean emitter = false;
        boolean consumer = true;

        // when
        CrossEnvironmentCommunicationProperties properties =
            new CrossEnvironmentCommunicationProperties(emitter, consumer);

        // then
        assertThat(properties.emitter()).isFalse();
        assertThat(properties.consumer()).isTrue();
    }

    @Test
    @DisplayName("Should create properties with both emitter and consumer enabled")
    void testBothEnabled() {
        // given & when
        CrossEnvironmentCommunicationProperties properties =
            new CrossEnvironmentCommunicationProperties(true, true);

        // then
        assertThat(properties.emitter()).isTrue();
        assertThat(properties.consumer()).isTrue();
    }

    @Test
    @DisplayName("Should create properties with both emitter and consumer disabled")
    void testBothDisabled() {
        // given & when
        CrossEnvironmentCommunicationProperties properties =
            new CrossEnvironmentCommunicationProperties(false, false);

        // then
        assertThat(properties.emitter()).isFalse();
        assertThat(properties.consumer()).isFalse();
    }


}