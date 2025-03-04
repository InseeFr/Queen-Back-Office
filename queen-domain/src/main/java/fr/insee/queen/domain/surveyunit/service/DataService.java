package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface DataService {
    ObjectNode getData(String surveyUnitId);

    void saveData(String surveyUnitId, ObjectNode dataValue);

    void updateCollectedData(String surveyUnitId, ObjectNode collectedData);
}
