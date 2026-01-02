package fr.insee.queen.jms.service.consummers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.modelefiliere.EventPayloadDto;
import fr.insee.modelefiliere.EventPayloadLeafStatesInnerDto;
import fr.insee.queen.domain.interrogation.model.LeafState;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireLeafStatesUpdatedEventConsumerTest {

    @Mock
    private StateDataService stateDataService;

    @Mock
    private Clock clock;

    @InjectMocks
    private QuestionnaireLeafStatesUpdatedEventConsumer consumer;

    private static final String INTERROGATION_ID = "INT-001";
    private static final UUID CORRELATION_ID = UUID.randomUUID();
    private static final long FIXED_TIME = 1234567890000L;
    private static final long OLD_TIME = 1234567880000L;
    private static final long LEAF_STATE_DATE = 1234567850000L;

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(Instant.ofEpochMilli(FIXED_TIME));
        lenient().when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    void shouldUpdateLeafStatesWhenStateDataExists() throws StateDataInvalidDateException {
        // Given
        StateData existingStateData = new StateData(StateDataType.INIT, OLD_TIME, "3");
        when(stateDataService.findStateData(INTERROGATION_ID)).thenReturn(Optional.of(existingStateData));

        EventPayloadLeafStatesInnerDto leafState1 = new EventPayloadLeafStatesInnerDto();
        leafState1.setState(EventPayloadLeafStatesInnerDto.StateEnum.INIT);
        leafState1.setDate(Instant.ofEpochMilli(LEAF_STATE_DATE));

        EventPayloadLeafStatesInnerDto leafState2 = new EventPayloadLeafStatesInnerDto();
        leafState2.setState(EventPayloadLeafStatesInnerDto.StateEnum.COMPLETED);
        leafState2.setDate(Instant.ofEpochMilli(LEAF_STATE_DATE + 1000));

        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);
        payload.setLeafStates(List.of(leafState1, leafState2));

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_LEAF_STATES_UPDATED);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        verify(stateDataService).findStateData(INTERROGATION_ID);

        ArgumentCaptor<StateData> stateDataCaptor = ArgumentCaptor.forClass(StateData.class);
        verify(stateDataService).saveStateData(eq(INTERROGATION_ID), stateDataCaptor.capture(), eq(false));

        StateData savedStateData = stateDataCaptor.getValue();
        assertThat(savedStateData.state()).isEqualTo(StateDataType.INIT);
        assertThat(savedStateData.currentPage()).isEqualTo("3");
        assertThat(savedStateData.date()).isGreaterThan(OLD_TIME);
        assertThat(savedStateData.leafStates()).hasSize(2);
        assertThat(savedStateData.leafStates().get(0).state()).isEqualTo("INIT");
        assertThat(savedStateData.leafStates().get(1).state()).isEqualTo("COMPLETED");
    }

    @Test
    void shouldNotSaveWhenStateDataDoesNotExist() throws StateDataInvalidDateException {
        // Given
        when(stateDataService.findStateData(INTERROGATION_ID)).thenReturn(Optional.empty());

        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_LEAF_STATES_UPDATED);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        verify(stateDataService).findStateData(INTERROGATION_ID);
        verify(stateDataService, never()).saveStateData(any(), any(), anyBoolean());
    }

    @Test
    void shouldNotProcessNonQuestionnaireLeafStatesUpdatedEvents() {
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
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_LEAF_STATES_UPDATED);
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
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_LEAF_STATES_UPDATED);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        verifyNoInteractions(stateDataService);
    }

    @Test
    void shouldHandleEmptyLeafStates() throws StateDataInvalidDateException {
        // Given
        StateData existingStateData = new StateData(StateDataType.COMPLETED, OLD_TIME, "10", List.of(
                new LeafState("INIT", LEAF_STATE_DATE)
        ));
        when(stateDataService.findStateData(INTERROGATION_ID)).thenReturn(Optional.of(existingStateData));

        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);
        payload.setLeafStates(List.of());

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_LEAF_STATES_UPDATED);
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
        assertThat(savedStateData.leafStates()).isEmpty();
    }

    @Test
    void shouldPreserveStateAndCurrentPageWhileUpdatingLeafStates() throws StateDataInvalidDateException {
        // Given
        StateData existingStateData = new StateData(StateDataType.VALIDATED, OLD_TIME, "5");
        when(stateDataService.findStateData(INTERROGATION_ID)).thenReturn(Optional.of(existingStateData));

        EventPayloadLeafStatesInnerDto leafState = new EventPayloadLeafStatesInnerDto();
        leafState.setState(EventPayloadLeafStatesInnerDto.StateEnum.COMPLETED);
        leafState.setDate(Instant.ofEpochMilli(LEAF_STATE_DATE));

        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(INTERROGATION_ID);
        payload.setLeafStates(List.of(leafState));

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_LEAF_STATES_UPDATED);
        eventDto.setCorrelationId(CORRELATION_ID);
        eventDto.setPayload(payload);

        // When
        consumer.consume(eventDto);

        // Then
        ArgumentCaptor<StateData> stateDataCaptor = ArgumentCaptor.forClass(StateData.class);
        verify(stateDataService).saveStateData(eq(INTERROGATION_ID), stateDataCaptor.capture(), eq(false));

        StateData savedStateData = stateDataCaptor.getValue();
        assertThat(savedStateData.state()).isEqualTo(StateDataType.VALIDATED);
        assertThat(savedStateData.currentPage()).isEqualTo("5");
        assertThat(savedStateData.leafStates()).hasSize(1);
        assertThat(savedStateData.leafStates().get(0).state()).isEqualTo("COMPLETED");
        assertThat(savedStateData.leafStates().get(0).date()).isEqualTo(LEAF_STATE_DATE);
    }
}
