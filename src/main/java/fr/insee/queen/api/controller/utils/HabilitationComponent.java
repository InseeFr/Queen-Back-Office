package fr.insee.queen.api.controller.utils;

import fr.insee.queen.api.service.pilotage.PilotageRole;
import org.springframework.security.core.Authentication;

public interface HabilitationComponent {
    void checkHabilitations(Authentication auth, String surveyUnitId, PilotageRole... roles);
}
