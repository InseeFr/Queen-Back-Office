package fr.insee.queen.domain.interrogation.service.dummy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.service.DataService;
import lombok.Getter;

public class DataFakeService implements DataService {
    @Getter
    private JsonNode dataSaved = null;

    @Override
    public ObjectNode getData(String interrogationId) {
        return null;
    }

    @Override
    public void saveData(String interrogationId, ObjectNode dataValue) {
        dataSaved = dataValue;
    }

    @Override
    public void updateCollectedData(String interrogationId, ObjectNode collectedData) {
        dataSaved = collectedData;
    }

    @Override
    public void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp) {
        // not used at this moment
    }
}
