package fr.insee.queen.domain.events.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.events.model.Event;

import java.util.List;
import java.util.UUID;

public interface EventsService {
    void createEvent(ObjectNode value);

    List<Event> getAllNewEvents();

    void ackEvent(UUID id);

    void publishEvent(ObjectNode value);
}
