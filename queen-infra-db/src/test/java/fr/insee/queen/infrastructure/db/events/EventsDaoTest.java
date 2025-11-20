package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventsDaoTest {

    @Mock
    private EventsJpaRepository<OutboxDB, UUID> jpaRepository;

    private EventsDao eventsDao;

    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        eventsDao = new EventsDao(jpaRepository);
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("When creating an event, then JPA repository is called with correct parameters")
    void testCreateEvent() {
        // given
        UUID eventId = UUID.randomUUID();
        ObjectNode event = objectMapper.createObjectNode();
        event.put("type", "QUESTIONNAIRE_INIT");
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("interrogationId", "interrogation-123");
        event.set("payload", payload);

        // when
        eventsDao.createEvent(eventId, event);

        // then
        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<ObjectNode> eventCaptor = ArgumentCaptor.forClass(ObjectNode.class);
        verify(jpaRepository).createEvent(idCaptor.capture(), eventCaptor.capture());

        UUID capturedId = idCaptor.getValue();
        ObjectNode capturedEvent = eventCaptor.getValue();

        assertThat(capturedId).isEqualTo(eventId);
        assertThat(capturedEvent).isEqualTo(event);
        assertThat(capturedEvent.get("type").asText()).isEqualTo("QUESTIONNAIRE_INIT");
        assertThat(capturedEvent.get("payload").get("interrogationId").asText()).isEqualTo("interrogation-123");
    }

    @Test
    @DisplayName("When creating multiple events, then JPA repository is called for each event")
    void testCreateEvent_MultipleEvents() {
        // given
        UUID eventId1 = UUID.randomUUID();
        ObjectNode event1 = objectMapper.createObjectNode();
        event1.put("type", "QUESTIONNAIRE_INIT");
        event1.set("payload", objectMapper.createObjectNode());

        UUID eventId2 = UUID.randomUUID();
        ObjectNode event2 = objectMapper.createObjectNode();
        event2.put("type", "QUESTIONNAIRE_COMPLETE");
        event2.set("payload", objectMapper.createObjectNode());

        // when
        eventsDao.createEvent(eventId1, event1);
        eventsDao.createEvent(eventId2, event2);

        // then
        verify(jpaRepository).createEvent(eventId1, event1);
        verify(jpaRepository).createEvent(eventId2, event2);
    }
}
