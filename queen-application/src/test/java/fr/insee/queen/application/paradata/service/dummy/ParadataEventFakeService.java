package fr.insee.queen.application.paradata.service.dummy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.paradata.service.ParadataEventService;
import lombok.Getter;

public class ParadataEventFakeService implements ParadataEventService {
    @Getter
    private boolean created = false;

    @Override
    public void createParadataEvent(String surveyUnitId, ObjectNode paradataValue) {
        this.created = true;
    }
}
