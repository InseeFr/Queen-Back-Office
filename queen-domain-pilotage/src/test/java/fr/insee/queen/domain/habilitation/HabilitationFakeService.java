package fr.insee.queen.domain.habilitation;

import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.pilotage.service.HabilitationService;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import lombok.Setter;

public class HabilitationFakeService implements HabilitationService {

    @Setter
    private boolean habilitationResult = true;

    @Override
    public boolean hasHabilitation(InterrogationSummary interrogation, PilotageRole role, String idep) {
        return habilitationResult;
    }
}