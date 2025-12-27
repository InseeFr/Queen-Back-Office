package fr.insee.queen.domain.synchronisation.service;

import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import fr.insee.queen.domain.interrogation.gateway.StateDataRepository;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.synchronisation.gateway.SynchronisationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SynchronisationApiService implements SynchronisationService {

    private final SynchronisationRepository synchronisationRepository;
    private final InterrogationRepository interrogationRepository;
    private final StateDataRepository stateDataRepository;

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

        log.info("Synchronisation completed for interrogation {}", interrogationId);
    }
}
