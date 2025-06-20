package fr.insee.queen.domain.events.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface EventBroker {
    void publishEvent(ObjectNode event);
}
