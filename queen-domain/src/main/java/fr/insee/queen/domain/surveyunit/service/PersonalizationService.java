package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface PersonalizationService {
    ArrayNode getPersonalization(String surveyUnitId);

    void updatePersonalization(String surveyUnitId, ArrayNode personalizationValue);
}
