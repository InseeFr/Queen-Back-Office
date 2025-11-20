package fr.insee.queen.domain.event.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface EventService {
    void saveEvent(ObjectNode event);
}
