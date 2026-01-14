package fr.insee.queen.domain.pilotage.service;

import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.pilotage.gateway.PilotageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "application.collection.environment", havingValue = "WEB", matchIfMissing = true)
public class HabilitationWebService implements HabilitationService {

    private final PilotageRepository pilotageRepository;

    @Override
    public boolean hasHabilitation(InterrogationSummary interrogation, PilotageRole role, String idep) {
        return pilotageRepository.hasHabilitation(interrogation, role, idep);
    }
}