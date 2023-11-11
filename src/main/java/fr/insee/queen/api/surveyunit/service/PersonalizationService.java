package fr.insee.queen.api.surveyunit.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface PersonalizationService {
    String getPersonalization(String surveyUnitId);

    void updatePersonalization(String surveyUnitId, JsonNode commentValue);
}
