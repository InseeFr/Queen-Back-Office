package fr.insee.queen.infrastructure.broker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BrokerMessageTest {

    @Test
    @DisplayName("BrokerMessage should be created with type and payload")
    void testBrokerMessageCreation() {
        BrokerMessage.Payload payload = new BrokerMessage.Payload("123", Collections.emptyList());
        BrokerMessage message = new BrokerMessage("TEST_TYPE", payload);

        assertThat(message.type()).isEqualTo("TEST_TYPE");
        assertThat(message.payload()).isEqualTo(payload);
    }

    @Test
    @DisplayName("Payload should return false when leafStates is null")
    void testPayloadHasLeafStatesReturnsFalseWhenNull() {
        BrokerMessage.Payload payload = new BrokerMessage.Payload("123", null);

        assertThat(payload.hasLeafStates()).isFalse();
    }

    @Test
    @DisplayName("Payload should return false when leafStates is empty")
    void testPayloadHasLeafStatesReturnsFalseWhenEmpty() {
        BrokerMessage.Payload payload = new BrokerMessage.Payload("123", Collections.emptyList());

        assertThat(payload.hasLeafStates()).isFalse();
    }

    @Test
    @DisplayName("Payload should return true when leafStates is not empty")
    void testPayloadHasLeafStatesReturnsTrueWhenNotEmpty() {
        BrokerMessage.LeafState leafState = new BrokerMessage.LeafState(123456789L, "VALIDATED");
        BrokerMessage.Payload payload = new BrokerMessage.Payload("123", List.of(leafState));

        assertThat(payload.hasLeafStates()).isTrue();
    }

    @Test
    @DisplayName("LeafState should be created with date and state")
    void testLeafStateCreation() {
        BrokerMessage.LeafState leafState = new BrokerMessage.LeafState(123456789L, "VALIDATED");

        assertThat(leafState.date()).isEqualTo(123456789L);
        assertThat(leafState.state()).isEqualTo("VALIDATED");
    }
}
