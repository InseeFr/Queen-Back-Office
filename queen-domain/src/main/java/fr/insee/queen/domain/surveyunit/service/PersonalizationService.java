package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface PersonalizationService {
    String getPersonalization(String surveyUnitId);

    void updatePersonalization(String surveyUnitId, JsonNode commentValue);
}
