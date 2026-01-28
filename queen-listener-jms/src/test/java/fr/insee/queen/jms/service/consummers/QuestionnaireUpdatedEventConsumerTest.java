package fr.insee.queen.jms.service.consummers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.modelefiliere.EventPayloadDto;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireUpdatedEventConsumerTest {

    @Mock
    private StateDataService stateDataService;

    @Mock
    private Clock clock;

    @InjectMocks
    private QuestionnaireUpdatedEventConsumer consumer;

    private static final String INTERROGATION_ID = "INT-001";
    private static final UUID CORRELATION_ID = UUID.randomUUID();
    private static final long FIXED_TIME = 1234567890000L;
    private static final long OLD_TIME = 1234567880000L;

    @BeforeEach
    void setUp() {
        // Configure fixed clock for consistent timestamps
        lenient().when(clock.instant()).thenReturn(Instant.ofEpochMilli(FIXED_TIME));
        lenient().when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    void shouldRefreshDateWhenStateDataExists() throws StateDataInvalidDateException {
        // Given
        StateData existingStateData = new StateData(StateDataType.INIT, OLD_TIME, "3");
        when(stateDataService.findStateData(INTERROGATION_ID)).thenReturn(Optional.of(existingStateData));

        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_UPDATED);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        verify(stateDataService).findStateData(INTERROGATION_ID);

        ArgumentCaptor<StateData> stateDataCaptor = ArgumentCaptor.forClass(StateData.class);
        verify(stateDataService).saveStateData(eq(INTERROGATION_ID), stateDataCaptor.capture(), eq(false));

        StateData savedStateData = stateDataCaptor.getValue();
        assertThat(savedStateData.state()).isEqualTo(StateDataType.INIT); // State unchanged
        assertThat(savedStateData.currentPage()).isEqualTo("3"); // CurrentPage unchanged
        assertThat(savedStateData.date()).isGreaterThan(OLD_TIME); // Date refreshed
    }

    @Test
    void shouldNotSaveWhenStateDataDoesNotExist() throws StateDataInvalidDateException {
        // Given
        when(stateDataService.findStateData(INTERROGATION_ID)).thenReturn(Optional.empty());

        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_UPDATED);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        verify(stateDataService).findStateData(INTERROGATION_ID);
        verify(stateDataService, never()).saveStateData(any(), any(), anyBoolean());
    }

    @Test
    void shouldNotProcessNonQuestionnaireUpdatedEvents() {
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
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_UPDATED);
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
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_UPDATED);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        verifyNoInteractions(stateDataService);
    }

    @Test
    void shouldPreserveAllFieldsExceptDate() throws StateDataInvalidDateException {
        // Given
        StateData existingStateData = new StateData(StateDataType.COMPLETED, OLD_TIME, "10");
        when(stateDataService.findStateData(INTERROGATION_ID)).thenReturn(Optional.of(existingStateData));

        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_UPDATED);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        ArgumentCaptor<StateData> stateDataCaptor = ArgumentCaptor.forClass(StateData.class);
        verify(stateDataService).saveStateData(eq(INTERROGATION_ID), stateDataCaptor.capture(), eq(false));

        StateData savedStateData = stateDataCaptor.getValue();
        assertThat(savedStateData.state()).isEqualTo(StateDataType.COMPLETED);
        assertThat(savedStateData.currentPage()).isEqualTo("10");
        assertThat(savedStateData.date()).isNotEqualTo(OLD_TIME);
    }
}