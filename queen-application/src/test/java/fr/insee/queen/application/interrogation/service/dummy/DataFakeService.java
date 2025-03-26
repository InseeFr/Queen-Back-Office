package fr.insee.queen.application.interrogation.service.dummy;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.service.DataService;
import lombok.Getter;

public class DataFakeService implements DataService {
    @Getter
    private boolean checkUpdateData = false;

    @Override
    public ObjectNode getData(String interrogationId) {
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("data", "data-value");
        return data;
    }

    @Override
    public void saveData(String interrogationId, ObjectNode dataValue) {
        checkUpdateData = true;
    }

    @Override
    public void updateCollectedData(String interrogationId, ObjectNode collectedData) {
        checkUpdateData = true;
    }

    @Override
    public void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp) {
        // not used at this moment
    }
}
