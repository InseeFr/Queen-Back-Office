package fr.insee.queen.api.pilotage.controller.dummy;

import fr.insee.queen.api.pilotage.controller.HabilitationComponent;
import fr.insee.queen.api.pilotage.service.PilotageRole;
import lombok.Getter;

public class HabilitationFakeComponent implements HabilitationComponent {
    @Getter
    private boolean checked = false;

    @Override
    public void checkHabilitations(String surveyUnitId, PilotageRole... roles) {
        checked = true;
    }
}
