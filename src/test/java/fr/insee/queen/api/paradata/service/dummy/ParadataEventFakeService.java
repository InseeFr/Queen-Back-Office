package fr.insee.queen.api.paradata.service.dummy;

import fr.insee.queen.api.paradata.service.ParadataEventService;
import lombok.Getter;

public class ParadataEventFakeService implements ParadataEventService {
    @Getter
    private boolean created = false;

    @Override
    public void createParadataEvent(String surveyUnitId, String paradataValue) {
        this.created = true;
    }
}
