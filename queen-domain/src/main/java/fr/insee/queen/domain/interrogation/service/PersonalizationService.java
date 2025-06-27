package fr.insee.queen.domain.interrogation.service;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface PersonalizationService {
    ArrayNode getPersonalization(String interrogationId);

    void updatePersonalization(String interrogationId, ArrayNode personalizationValue);
}
