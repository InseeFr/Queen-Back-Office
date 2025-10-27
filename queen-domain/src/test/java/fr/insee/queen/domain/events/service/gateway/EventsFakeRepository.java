package fr.insee.queen.domain.events.service.gateway;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.events.gateway.EventsRepository;
import lombok.Getter;

import java.util.UUID;

public class EventsFakeRepository implements EventsRepository {
    @Getter
    private boolean created = false;

    @Override
    public void createEvent(UUID id, ObjectNode paradataValue) {
        this.created = true;
    }
}
