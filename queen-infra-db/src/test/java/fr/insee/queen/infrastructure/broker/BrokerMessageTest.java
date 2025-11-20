package fr.insee.queen.infrastructure.broker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BrokerMessageTest {

    @Test
    @DisplayName("When creating a BrokerMessage, then all fields are correctly set")
    void testBrokerMessage_Creation() {
        // given
        BrokerMessage.LeafState leafState = new BrokerMessage.LeafState(1234567890L, "COMPLETED");
        BrokerMessage.Payload payload = new BrokerMessage.Payload("interrogation-123", List.of(leafState));

        // when
        BrokerMessage message = new BrokerMessage("QUESTIONNAIRE_INIT", payload);

        // then
        assertThat(message.type()).isEqualTo("QUESTIONNAIRE_INIT");
        assertThat(message.payload()).isEqualTo(payload);
        assertThat(message.payload().interrogationId()).isEqualTo("interrogation-123");
        assertThat(message.payload().leafStates()).hasSize(1);
        assertThat(message.payload().leafStates().get(0).date()).isEqualTo(1234567890L);
        assertThat(message.payload().leafStates().get(0).state()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("When payload has leaf states, then hasLeafStates returns true")
    void testPayload_HasLeafStates_True() {
        // given
        BrokerMessage.LeafState leafState = new BrokerMessage.LeafState(1234567890L, "IN_PROGRESS");
        BrokerMessage.Payload payload = new BrokerMessage.Payload("interrogation-456", List.of(leafState));

        // when & then
        assertThat(payload.hasLeafStates()).isTrue();
    }

    @Test
    @DisplayName("When payload has empty leaf states list, then hasLeafStates returns false")
    void testPayload_HasLeafStates_EmptyList() {
        // given
        BrokerMessage.Payload payload = new BrokerMessage.Payload("interrogation-789", Collections.emptyList());

        // when & then
        assertThat(payload.hasLeafStates()).isFalse();
    }

    @Test
    @DisplayName("When payload has null leaf states, then hasLeafStates returns false")
    void testPayload_HasLeafStates_Null() {
        // given
        BrokerMessage.Payload payload = new BrokerMessage.Payload("interrogation-999", null);

        // when & then
        assertThat(payload.hasLeafStates()).isFalse();
    }

    @Test
    @DisplayName("When payload has multiple leaf states, then hasLeafStates returns true")
    void testPayload_HasLeafStates_MultipleStates() {
        // given
        BrokerMessage.LeafState leafState1 = new BrokerMessage.LeafState(1234567890L, "IN_PROGRESS");
        BrokerMessage.LeafState leafState2 = new BrokerMessage.LeafState(1234567900L, "COMPLETED");
        BrokerMessage.Payload payload = new BrokerMessage.Payload("interrogation-111", List.of(leafState1, leafState2));

        // when & then
        assertThat(payload.hasLeafStates()).isTrue();
        assertThat(payload.leafStates()).hasSize(2);
    }

    @Test
    @DisplayName("When creating a LeafState, then all fields are correctly set")
    void testLeafState_Creation() {
        // given
        Long date = 1234567890L;
        String state = "VALIDATED";

        // when
        BrokerMessage.LeafState leafState = new BrokerMessage.LeafState(date, state);

        // then
        assertThat(leafState.date()).isEqualTo(date);
        assertThat(leafState.state()).isEqualTo(state);
    }

}
