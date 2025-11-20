package fr.insee.queen.domain.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.event.gateway.EventsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link EventsApiService}.
 * <p>
 * These tests verify that the service correctly delegates event creation
 * to the EventsRepository with proper UUID generation.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class EventsApiServiceTest {

    @Mock
    private EventsRepository eventsRepository;

    private EventsApiService eventsApiService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        eventsApiService = new EventsApiService(eventsRepository);
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("When saving an event, then repository createEvent is called with UUID and event")
    void testSaveEvent() {
        // given
        ObjectNode event = objectMapper.createObjectNode();
        event.put("type", "QUESTIONNAIRE_INIT");
        event.set("payload", objectMapper.createObjectNode());

        // when
        eventsApiService.saveEvent(event);

        // then
        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<ObjectNode> eventCaptor = ArgumentCaptor.forClass(ObjectNode.class);
        verify(eventsRepository).createEvent(uuidCaptor.capture(), eventCaptor.capture());

        UUID capturedUuid = uuidCaptor.getValue();
        ObjectNode capturedEvent = eventCaptor.getValue();

        assertThat(capturedUuid).isNotNull();
        assertThat(capturedEvent.get("type").asText()).isEqualTo("QUESTIONNAIRE_INIT");
        assertThat(capturedEvent.get("payload")).isNotNull();
    }

    @Test
    @DisplayName("When saving multiple events, then each event is saved with a unique UUID")
    void testSaveEvent_MultipleEvents() {
        // given
        ObjectNode event1 = objectMapper.createObjectNode();
        event1.put("type", "QUESTIONNAIRE_INIT");
        event1.set("payload", objectMapper.createObjectNode());

        ObjectNode event2 = objectMapper.createObjectNode();
        event2.put("type", "QUESTIONNAIRE_COMPLETE");
        event2.set("payload", objectMapper.createObjectNode());

        // when
        eventsApiService.saveEvent(event1);
        eventsApiService.saveEvent(event2);

        // then
        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(eventsRepository).createEvent(uuidCaptor.capture(), eq(event1));
        verify(eventsRepository).createEvent(uuidCaptor.capture(), eq(event2));

        // Verify that different UUIDs are generated for each event
        assertThat(uuidCaptor.getAllValues()).hasSize(2);
        assertThat(uuidCaptor.getAllValues().get(0)).isNotEqualTo(uuidCaptor.getAllValues().get(1));
    }

    @Test
    @DisplayName("When saving a large event payload, then event is correctly saved")
    void testSaveEvent_LargePayload() {
        // given
        ObjectNode event = objectMapper.createObjectNode();
        event.put("type", "LARGE_EVENT");

        ObjectNode payload = objectMapper.createObjectNode();
        // Simulate a large payload with many fields
        for (int i = 0; i < 100; i++) {
            payload.put("field_" + i, "value_" + i);
        }
        event.set("payload", payload);

        // when
        eventsApiService.saveEvent(event);

        // then
        ArgumentCaptor<ObjectNode> eventCaptor = ArgumentCaptor.forClass(ObjectNode.class);
        verify(eventsRepository).createEvent(any(UUID.class), eventCaptor.capture());

        ObjectNode capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.get("payload").size()).isEqualTo(100);
    }
}
