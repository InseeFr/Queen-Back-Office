package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxDBTest {

    @Test
    @DisplayName("Should create OutboxDB with no-args constructor")
    void noArgsConstructor_shouldCreateOutboxDB() {
        // When
        OutboxDB outbox = new OutboxDB();

        // Then
        assertThat(outbox).isNotNull();
        assertThat(outbox.getId()).isNull();
        assertThat(outbox.getPayload()).isNull();
        assertThat(outbox.getCreatedDate()).isNull();
    }

    @Test
    @DisplayName("Should create OutboxDB with custom constructor")
    void customConstructor_shouldCreateOutboxDBWithIdAndPayload() {
        // Given
        UUID id = UUID.randomUUID();
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("key", "value");

        // When
        OutboxDB outbox = new OutboxDB(id, payload);

        // Then
        assertThat(outbox).isNotNull();
        assertThat(outbox.getId()).isEqualTo(id);
        assertThat(outbox.getPayload()).isEqualTo(payload);
        assertThat(outbox.getCreatedDate()).isNull();
    }

    @Test
    @DisplayName("Should set and get id correctly")
    void setId_shouldSetIdCorrectly() {
        // Given
        OutboxDB outbox = new OutboxDB();
        UUID id = UUID.randomUUID();

        // When
        outbox.setId(id);

        // Then
        assertThat(outbox.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Should set and get payload correctly")
    void setPayload_shouldSetPayloadCorrectly() {
        // Given
        OutboxDB outbox = new OutboxDB();
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("eventType", "survey_unit_created");
        payload.put("aggregateId", "123");

        // When
        outbox.setPayload(payload);

        // Then
        assertThat(outbox.getPayload()).isEqualTo(payload);
        assertThat(outbox.getPayload().get("eventType").asText()).isEqualTo("survey_unit_created");
        assertThat(outbox.getPayload().get("aggregateId").asText()).isEqualTo("123");
    }

    @Test
    @DisplayName("Should set and get createdDate correctly")
    void setCreatedDate_shouldSetCreatedDateCorrectly() {
        // Given
        OutboxDB outbox = new OutboxDB();
        LocalDateTime createdDate = LocalDateTime.now();

        // When
        outbox.setCreatedDate(createdDate);

        // Then
        assertThat(outbox.getCreatedDate()).isEqualTo(createdDate);
    }
}