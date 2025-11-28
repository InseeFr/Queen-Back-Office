package fr.insee.queen.application.crossenvironmentcommunication.service.dummy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.event.service.EventService;
import lombok.Getter;

public class EventFakeService implements EventService {
    @Getter
    private boolean saved = false;

    @Getter
    private ObjectNode savedEvent = null;

    @Override
    public void saveEvent(ObjectNode event) {
        this.saved = true;
        this.savedEvent = event;
    }

    public void reset() {
        this.saved = false;
        this.savedEvent = null;
    }
}