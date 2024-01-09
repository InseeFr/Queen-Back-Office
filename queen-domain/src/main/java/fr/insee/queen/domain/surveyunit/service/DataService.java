package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface DataService {
    String getData(String surveyUnitId);

    void updateData(String surveyUnitId, JsonNode commentValue);
}
