package fr.insee.queen.api.controller.dummy;

import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.service.pilotage.PilotageRole;
import lombok.Getter;
import org.springframework.security.core.Authentication;

public class HabilitationFakeComponent implements HabilitationComponent {
    @Getter
    private boolean checked = false;

    @Override
    public void checkHabilitations(Authentication auth, String surveyUnitId, PilotageRole... roles) {
        checked = true;
    }
}
