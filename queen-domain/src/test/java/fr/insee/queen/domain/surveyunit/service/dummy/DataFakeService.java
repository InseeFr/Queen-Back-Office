package fr.insee.queen.domain.surveyunit.service.dummy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunit.service.DataService;
import lombok.Getter;

public class DataFakeService implements DataService {
    @Getter
    private JsonNode dataSaved = null;

    @Override
    public ObjectNode getData(String surveyUnitId) {
        return null;
    }

    @Override
    public void saveData(String surveyUnitId, ObjectNode dataValue) {
        dataSaved = dataValue;
    }

    @Override
    public void updateCollectedData(String surveyUnitId, ObjectNode collectedData) {
        dataSaved = collectedData;
    }

    @Override
    public void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp) {

    }
}
