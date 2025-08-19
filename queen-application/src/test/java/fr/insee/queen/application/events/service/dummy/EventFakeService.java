package fr.insee.queen.application.events.service.dummy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.events.service.EventsService;
import lombok.Getter;

public class EventFakeService implements EventsService {
    @Getter
    private boolean created = false;

    @Override
    public void createEvent(ObjectNode value) {
        this.created = true;
    }
}
