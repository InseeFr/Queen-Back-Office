package fr.insee.queen.domain.paradata.service;

import tools.jackson.databind.node.ObjectNode;

public interface ParadataEventService {
    void createParadataEvent(String interrogationId, ObjectNode paradataValue);
}
