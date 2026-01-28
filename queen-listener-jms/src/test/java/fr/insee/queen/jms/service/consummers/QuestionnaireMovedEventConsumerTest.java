package fr.insee.queen.jms.service.consummers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.modelefiliere.EventPayloadDto;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireMovedEventConsumerTest {

    @Mock
    private StateDataService stateDataService;

    @Mock
    private InterrogationService interrogationService;

    @Mock
    private Clock clock;

    private QuestionnaireMovedEventConsumer consumer;

    private static final String INTERROGATION_ID = "INT-001";
    private static final UUID CORRELATION_ID = UUID.randomUUID();
    private static final long FIXED_TIME = 1234567890000L;

    @BeforeEach
    void setUp() {
        consumer = new QuestionnaireMovedEventConsumer(stateDataService, clock, interrogationService);
        // Configure fixed clock for consistent timestamps (lenient for tests that don't need it)
        lenient().when(clock.instant()).thenReturn(Instant.ofEpochMilli(FIXED_TIME));
        lenient().when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    void shouldProcessMultimodeMovedEvent() throws StateDataInvalidDateException {
        // Given
        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.MULTIMODE_MOVED);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        ArgumentCaptor<StateData> stateDataCaptor = ArgumentCaptor.forClass(StateData.class);
        verify(stateDataService).saveStateData(eq(INTERROGATION_ID), stateDataCaptor.capture(), eq(false));

        StateData savedStateData = stateDataCaptor.getValue();
        assertThat(savedStateData.state()).isEqualTo(StateDataType.IS_MOVED);
        assertThat(savedStateData.currentPage()).isEqualTo("1");
        assertThat(savedStateData.date()).isGreaterThan(0);
    }

    @Test
    void shouldNotProcessNonMultimodeMovedEvents() {
        // Given
        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        verifyNoInteractions(stateDataService);
    }

    @Test
    void shouldNotProcessEventWithoutPayload() {
        // Given
        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.MULTIMODE_MOVED);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(null);

        // When
        consumer.consume(eventDto);

        // Then
        verifyNoInteractions(stateDataService);
    }

    @Test
    void shouldNotProcessEventWithoutInterrogationId() {
        // Given
        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(null);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.MULTIMODE_MOVED);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        verifyNoInteractions(stateDataService);
    }

    @Test
    @DisplayName("canConsume should return true when interrogation is not locked")
    void canConsumeShouldReturnTrueWhenInterrogationIsNotLocked() {
        // Given
        Interrogation interrogation = new Interrogation(
                INTERROGATION_ID, "survey-unit-id", "campaign-id", "questionnaire-id",
                null, null, null, null, null, false);
        when(interrogationService.getInterrogation(INTERROGATION_ID)).thenReturn(interrogation);

        // When
        boolean result = consumer.canConsume(INTERROGATION_ID);

        // Then
        assertThat(result).isTrue();
        verify(interrogationService).getInterrogation(INTERROGATION_ID);
    }

    @Test
    @DisplayName("canConsume should return false when interrogation is locked")
    void canConsumeShouldReturnFalseWhenInterrogationIsLocked() {
        // Given
        Interrogation interrogation = new Interrogation(
                INTERROGATION_ID, "survey-unit-id", "campaign-id", "questionnaire-id",
                null, null, null, null, null, true);
        when(interrogationService.getInterrogation(INTERROGATION_ID)).thenReturn(interrogation);

        // When
        boolean result = consumer.canConsume(INTERROGATION_ID);

        // Then
        assertThat(result).isFalse();
        verify(interrogationService).getInterrogation(INTERROGATION_ID);
    }

    @Test
    @DisplayName("canConsume should return true when locked is null")
    void canConsumeShouldReturnTrueWhenLockedIsNull() {
        // Given
        Interrogation interrogation = new Interrogation(
                INTERROGATION_ID, "survey-unit-id", "campaign-id", "questionnaire-id",
                null, null, null, null, null, null);
        when(interrogationService.getInterrogation(INTERROGATION_ID)).thenReturn(interrogation);

        // When
        boolean result = consumer.canConsume(INTERROGATION_ID);

        // Then
        assertThat(result).isTrue();
        verify(interrogationService).getInterrogation(INTERROGATION_ID);
    }

    @Test
    @DisplayName("canConsume should return true when interrogation is not found")
    void canConsumeShouldReturnTrueWhenInterrogationNotFound() {
        // Given
        when(interrogationService.getInterrogation(INTERROGATION_ID))
                .thenThrow(new EntityNotFoundException("Interrogation not found"));

        // When
        boolean result = consumer.canConsume(INTERROGATION_ID);

        // Then
        assertThat(result).isTrue();
        verify(interrogationService).getInterrogation(INTERROGATION_ID);
    }

}