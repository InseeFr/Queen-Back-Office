package fr.insee.queen.domain.event.gateway;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.UUID;

public interface EventsRepository {
    void createEvent(UUID id, ObjectNode event);
}
