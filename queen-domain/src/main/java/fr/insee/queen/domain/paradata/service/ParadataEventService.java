package fr.insee.queen.domain.paradata.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ParadataEventService {
    void createParadataEvent(String surveyUnitId, ObjectNode paradataValue);
}
