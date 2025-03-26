package fr.insee.queen.domain.interrogation.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface DataService {
    ObjectNode getData(String interrogationId);

    void saveData(String interrogationId, ObjectNode dataValue);

    void updateCollectedData(String interrogationId, ObjectNode collectedData);

    void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp);
}
