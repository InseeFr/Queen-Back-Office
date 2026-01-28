package fr.insee.queen.domain.synchronisation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;

import java.util.UUID;
import fr.insee.queen.domain.interrogation.gateway.StateDataRepository;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.messaging.port.serverside.Publisher;
import fr.insee.queen.domain.synchronisation.gateway.SynchronisationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SynchronisationApiServiceTest {

    @Mock
    private SynchronisationRepository synchronisationRepository;

    @Mock
    private InterrogationRepository interrogationRepository;

    @Mock
    private StateDataRepository stateDataRepository;

    @Mock
    private Publisher publisher;

    private SynchronisationApiService synchronisationApiService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        synchronisationApiService = new SynchronisationApiService(
                synchronisationRepository,
                interrogationRepository,
                stateDataRepository,
                publisher
        );
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("When synchronising an interrogation, then data and stateData are persisted")
    void testSynchronise() {
        // given
        String interrogationId = "interrogation-id";
        ObjectNode data = objectMapper.createObjectNode();
        data.put("field", "value");
        ArrayNode personalization = objectMapper.createArrayNode();
        ObjectNode comment = objectMapper.createObjectNode();
        StateData stateData = new StateData(StateDataType.INIT, 1234567890L, "current-page");

        Interrogation expectedInterrogation = new Interrogation(
                interrogationId,
                "survey-unit-id",
                "campaign-id",
                "questionnaire-id",
                personalization,
                data,
                comment,
                stateData,
                null,
                null
        );

        when(synchronisationRepository.synchronise(interrogationId)).thenReturn(expectedInterrogation);

        // when
        synchronisationApiService.synchronise(interrogationId);

        // then
        verify(synchronisationRepository).synchronise(interrogationId);
        verify(interrogationRepository).saveData(interrogationId, data);
        verify(stateDataRepository).save(interrogationId, stateData);
        verify(publisher).publish(any(EventDto.class), any(UUID.class));
    }

    @Test
    @DisplayName("When synchronising with null response, then nothing is persisted")
    void testSynchronise_NullResponse() {
        // given
        String interrogationId = "interrogation-id";
        when(synchronisationRepository.synchronise(interrogationId)).thenReturn(null);

        // when
        synchronisationApiService.synchronise(interrogationId);

        // then
        verify(synchronisationRepository).synchronise(interrogationId);
        verifyNoInteractions(interrogationRepository);
        verifyNoInteractions(stateDataRepository);
        verifyNoInteractions(publisher);
    }

    @Test
    @DisplayName("When synchronising with null data, then only stateData is persisted")
    void testSynchronise_NullData() {
        // given
        String interrogationId = "interrogation-id";
        StateData stateData = new StateData(StateDataType.INIT, 1234567890L, "current-page");

        Interrogation interrogation = new Interrogation(
                interrogationId,
                "survey-unit-id",
                "campaign-id",
                "questionnaire-id",
                null,
                null,
                null,
                stateData,
                null,
                null
        );

        when(synchronisationRepository.synchronise(interrogationId)).thenReturn(interrogation);

        // when
        synchronisationApiService.synchronise(interrogationId);

        // then
        verify(synchronisationRepository).synchronise(interrogationId);
        verify(interrogationRepository, never()).saveData(anyString(), any());
        verify(stateDataRepository).save(interrogationId, stateData);
        verify(publisher).publish(any(EventDto.class), any(UUID.class));
    }

    @Test
    @DisplayName("When synchronising with null stateData, then only data is persisted")
    void testSynchronise_NullStateData() {
        // given
        String interrogationId = "interrogation-id";
        ObjectNode data = objectMapper.createObjectNode();
        data.put("field", "value");

        Interrogation interrogation = new Interrogation(
                interrogationId,
                "survey-unit-id",
                "campaign-id",
                "questionnaire-id",
                null,
                data,
                null,
                null,
                null,
                null
        );

        when(synchronisationRepository.synchronise(interrogationId)).thenReturn(interrogation);

        // when
        synchronisationApiService.synchronise(interrogationId);

        // then
        verify(synchronisationRepository).synchronise(interrogationId);
        verify(interrogationRepository).saveData(interrogationId, data);
        verify(stateDataRepository, never()).save(anyString(), any());
        verify(publisher).publish(any(EventDto.class), any(UUID.class));
    }
}
