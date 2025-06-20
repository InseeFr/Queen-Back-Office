package fr.insee.queen.domain.events.gateway;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.events.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventsRepository {
    void createEvent(UUID id, ObjectNode paradataValue);

    Optional<List<Event>> getAllNewEvents();

    void ack(UUID id);
}
