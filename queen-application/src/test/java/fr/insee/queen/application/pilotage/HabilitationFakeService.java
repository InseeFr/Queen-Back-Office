package fr.insee.queen.application.pilotage;

import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.pilotage.service.HabilitationService;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import lombok.Getter;
import lombok.Setter;

public class HabilitationFakeService implements HabilitationService {

    @Setter
    private boolean habilitationResult = true;

    @Getter
    private int wentThroughHasHabilitation = 0;

    @Override
    public boolean hasHabilitation(InterrogationSummary interrogation, PilotageRole role, String idep) {
        wentThroughHasHabilitation++;
        return habilitationResult;
    }
}