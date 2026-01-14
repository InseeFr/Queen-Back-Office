package fr.insee.queen.domain.pilotage.service;

import fr.insee.queen.domain.interrogation.model.InterrogationSummary;

public interface HabilitationService {
    boolean hasHabilitation(InterrogationSummary interrogation, PilotageRole role, String idep);
}