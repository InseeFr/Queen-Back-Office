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
class QuestionnaireInitEventConsumerTest {

    @Mock
    private StateDataService stateDataService;

    @Mock
    private Clock clock;

    @InjectMocks
    private QuestionnaireInitEventConsumer consumer;

    private static final String INTERROGATION_ID = "INT-001";
    private static final UUID CORRELATION_ID = UUID.randomUUID();
    private static final long FIXED_TIME = 1234567890000L;

    @BeforeEach
    void setUp() {
        // Configure fixed clock for consistent timestamps
        lenient().when(clock.instant()).thenReturn(Instant.ofEpochMilli(FIXED_TIME));
        lenient().when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    void shouldRefreshDateWhenStateDataExists() throws StateDataInvalidDateException {
        // Given
        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // Existing state with COMPLETED state
        StateData existingState = new StateData(StateDataType.COMPLETED, 1000L, "5");
        when(stateDataService.findStateData(INTERROGATION_ID)).thenReturn(Optional.of(existingState));

        // When
        consumer.consume(eventDto);

        // Then
        ArgumentCaptor<StateData> stateDataCaptor = ArgumentCaptor.forClass(StateData.class);
        verify(stateDataService).saveStateData(eq(INTERROGATION_ID), stateDataCaptor.capture(), eq(false));

        StateData savedStateData = stateDataCaptor.getValue();
        assertThat(savedStateData.state()).isEqualTo(StateDataType.COMPLETED); // State preserved
        assertThat(savedStateData.currentPage()).isEqualTo("5"); // Page preserved
        assertThat(savedStateData.date()).isGreaterThan(existingState.date()); // Date updated
    }

    @Test
    void shouldCreateInitStateWhenNoStateDataExists() throws StateDataInvalidDateException {
        // Given
        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        when(stateDataService.findStateData(INTERROGATION_ID)).thenReturn(Optional.empty());

        // When
        consumer.consume(eventDto);

        // Then
        ArgumentCaptor<StateData> stateDataCaptor = ArgumentCaptor.forClass(StateData.class);
        verify(stateDataService).saveStateData(eq(INTERROGATION_ID), stateDataCaptor.capture(), eq(false));

        StateData savedStateData = stateDataCaptor.getValue();
        assertThat(savedStateData.state()).isEqualTo(StateDataType.INIT);
        assertThat(savedStateData.currentPage()).isEqualTo("1");
        assertThat(savedStateData.date()).isGreaterThan(0);
    }

    @Test
    void shouldNotProcessNonQuestionnaireInitEvents() {
        // Given
        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_COMPLETED);
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
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
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
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        verifyNoInteractions(stateDataService);
    }
}