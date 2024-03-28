package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface DataService {
    String getData(String surveyUnitId);

    void saveData(String surveyUnitId, JsonNode dataValue);

    void updateCollectedData(String surveyUnitId, ObjectNode collectedData);
}
