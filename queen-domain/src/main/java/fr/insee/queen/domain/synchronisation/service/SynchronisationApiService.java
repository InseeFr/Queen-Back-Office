package fr.insee.queen.domain.synchronisation.service;

import fr.insee.modelefiliere.EventDto;
import fr.insee.modelefiliere.EventPayloadDto;
import fr.insee.modelefiliere.ModeDto;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import fr.insee.queen.domain.interrogation.gateway.StateDataRepository;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.messaging.port.serverside.Publisher;
import fr.insee.queen.domain.synchronisation.gateway.SynchronisationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class SynchronisationApiService implements SynchronisationService {

    private final SynchronisationRepository synchronisationRepository;
    private final InterrogationRepository interrogationRepository;
    private final StateDataRepository stateDataRepository;
    private final Optional<Publisher> publisher;

    public SynchronisationApiService(SynchronisationRepository synchronisationRepository,
                                     InterrogationRepository interrogationRepository,
                                     StateDataRepository stateDataRepository,
                                     @Autowired(required = false) Publisher publisher) {
        this.synchronisationRepository = synchronisationRepository;
        this.interrogationRepository = interrogationRepository;
        this.stateDataRepository = stateDataRepository;
        this.publisher = Optional.ofNullable(publisher);
    }

    @Override
    public void synchronise(String interrogationId) {
        log.info("Synchronising interrogation {}", interrogationId);

        Interrogation interrogation = synchronisationRepository.synchronise(interrogationId);

        if (interrogation == null) {
            log.warn("No interrogation found for id {}", interrogationId);
            return;
        }

        // Persist data
        if (interrogation.data() != null) {
            log.debug("Saving data for interrogation {}", interrogationId);
            interrogationRepository.saveData(interrogationId, interrogation.data());
        }

        // Persist stateData
        if (interrogation.stateData() != null) {
            log.debug("Saving stateData for interrogation {}", interrogationId);
            stateDataRepository.save(interrogationId, interrogation.stateData());
        }

        // Publish SWITCH_CAPI event
        publishSwitchCapiEvent(interrogationId);

        log.info("Synchronisation completed for interrogation {}", interrogationId);
    }

    private void publishSwitchCapiEvent(String interrogationId) {
        if (publisher.isEmpty()) {
            log.debug("Publisher not available, skipping QUESTIONNAIRE_SWITCH_CAPI event publication for interrogation {}", interrogationId);
            return;
        }

        log.debug("Publishing QUESTIONNAIRE_SWITCH_CAPI event for interrogation {}", interrogationId);

        EventDto eventDto = new EventDto(
                EventDto.EventTypeEnum.QUESTIONNAIRE_SWITCH_CAPI,
                EventDto.AggregateTypeEnum.QUESTIONNAIRE,
                new EventPayloadDto(interrogationId, ModeDto.CAPI)
        );

        publisher.get().publish(eventDto, UUID.randomUUID());

        log.info("QUESTIONNAIRE_SWITCH_CAPI event published for interrogation {}", interrogationId);
    }
}
