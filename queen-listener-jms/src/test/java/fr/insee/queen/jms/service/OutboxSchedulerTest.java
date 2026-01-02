package fr.insee.queen.jms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.infrastructure.db.events.EventsJpaRepository;
import fr.insee.queen.infrastructure.db.events.OutboxDB;
import fr.insee.queen.domain.messaging.port.serverside.Publisher;
import fr.insee.queen.infrastructure.jms.configuration.MultimodeProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxSchedulerTest {

    @Mock
    private EventsJpaRepository eventsJpaRepository;

    @Mock
    private Publisher publisher;

    @Mock
    private MultimodeProperties multimodeProperties;

    private OutboxScheduler outboxScheduler;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        outboxScheduler = new OutboxScheduler(eventsJpaRepository, publisher, multimodeProperties, objectMapper);
    }

    @Test
    void shouldProcessUnprocessedEvents() {
        // Given
        UUID eventId1 = UUID.randomUUID();
        UUID eventId2 = UUID.randomUUID();

        ObjectNode payload1 = createEventDtoPayload("INT-001", "CAPI");
        ObjectNode payload2 = createEventDtoPayload("INT-002", "CAWI");

        OutboxDB event1 = createOutboxEvent(eventId1, payload1);
        OutboxDB event2 = createOutboxEvent(eventId2, payload2);

        List<OutboxDB> unprocessedEvents = Arrays.asList(event1, event2);

        when(eventsJpaRepository.findUnprocessedEvents()).thenReturn(unprocessedEvents);

        // When
        outboxScheduler.processOutboxEvents();

        // Then
        verify(eventsJpaRepository, times(1)).findUnprocessedEvents();
        verify(publisher, times(2)).publish(any(EventDto.class), any(UUID.class));
        verify(eventsJpaRepository, times(1)).markAsProcessed(eq(eventId1), any(LocalDateTime.class));
        verify(eventsJpaRepository, times(1)).markAsProcessed(eq(eventId2), any(LocalDateTime.class));
    }

    @Test
    void shouldDoNothingWhenNoUnprocessedEvents() {
        // Given
        when(eventsJpaRepository.findUnprocessedEvents()).thenReturn(Collections.emptyList());

        // When
        outboxScheduler.processOutboxEvents();

        // Then
        verify(eventsJpaRepository, times(1)).findUnprocessedEvents();
        verify(publisher, never()).publish(any(EventDto.class), any(UUID.class));
        verify(eventsJpaRepository, never()).markAsProcessed(any(), any());
    }

    @Test
    void shouldContinueProcessingWhenOneEventFails() {
        // Given
        UUID eventId1 = UUID.randomUUID();
        UUID eventId2 = UUID.randomUUID();

        ObjectNode payload1 = createEventDtoPayload("INT-001", "CAPI");
        ObjectNode payload2 = createEventDtoPayload("INT-002", "CAWI");

        OutboxDB event1 = createOutboxEvent(eventId1, payload1);
        OutboxDB event2 = createOutboxEvent(eventId2, payload2);

        List<OutboxDB> unprocessedEvents = Arrays.asList(event1, event2);

        when(eventsJpaRepository.findUnprocessedEvents()).thenReturn(unprocessedEvents);
        doThrow(new RuntimeException("Publish failed")).when(publisher).publish(any(EventDto.class), eq(eventId1));

        // When
        outboxScheduler.processOutboxEvents();

        // Then
        verify(eventsJpaRepository, times(1)).findUnprocessedEvents();
        verify(publisher, times(1)).publish(any(EventDto.class), eq(eventId1));
        verify(publisher, times(1)).publish(any(EventDto.class), eq(eventId2)); // Should still process second event
        verify(eventsJpaRepository, never()).markAsProcessed(eq(eventId1), any()); // First event should not be marked
        verify(eventsJpaRepository, times(1)).markAsProcessed(eq(eventId2), any(LocalDateTime.class)); // Second event should be marked
    }

    @Test
    void shouldHandleRepositoryException() {
        // Given
        when(eventsJpaRepository.findUnprocessedEvents()).thenThrow(new RuntimeException("Database error"));

        // When
        outboxScheduler.processOutboxEvents();

        // Then
        verify(eventsJpaRepository, times(1)).findUnprocessedEvents();
        verify(publisher, never()).publish(any(EventDto.class), any(UUID.class));
        verify(eventsJpaRepository, never()).markAsProcessed(any(), any());
    }

    @Test
    void shouldMarkEventWithCorrectTimestamp() {
        // Given
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventDtoPayload("INT-001", "CAPI");

        OutboxDB event = createOutboxEvent(eventId, payload);
        List<OutboxDB> unprocessedEvents = Collections.singletonList(event);

        when(eventsJpaRepository.findUnprocessedEvents()).thenReturn(unprocessedEvents);

        LocalDateTime beforeExecution = LocalDateTime.now().minusSeconds(1);

        // When
        outboxScheduler.processOutboxEvents();

        LocalDateTime afterExecution = LocalDateTime.now().plusSeconds(1);

        // Then
        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(eventsJpaRepository, times(1)).markAsProcessed(eq(eventId), dateCaptor.capture());

        LocalDateTime capturedDate = dateCaptor.getValue();
        assertThat(capturedDate).isAfter(beforeExecution);
        assertThat(capturedDate).isBefore(afterExecution);
    }

    private OutboxDB createOutboxEvent(UUID id, ObjectNode payload) {
        OutboxDB outboxDB = new OutboxDB();
        outboxDB.setId(id);
        outboxDB.setPayload(payload);
        outboxDB.setCreatedDate(LocalDateTime.now());
        return outboxDB;
    }

    private ObjectNode createEventDtoPayload(String interrogationId, String mode) {
        ObjectNode eventPayload = objectMapper.createObjectNode();
        eventPayload.put("eventType", "QUESTIONNAIRE_INIT");
        eventPayload.put("aggregateType", "QUESTIONNAIRE");

        ObjectNode innerPayload = objectMapper.createObjectNode();
        innerPayload.put("interrogationId", interrogationId);
        innerPayload.put("mode", mode);

        eventPayload.set("payload", innerPayload);
        return eventPayload;
    }
}