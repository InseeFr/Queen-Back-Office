package fr.insee.queen.application.surveyunit.service.dummy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunit.service.DataService;
import lombok.Getter;

public class DataFakeService implements DataService {
    @Getter
    private boolean checkUpdateCollectedData = false;

    @Override
    public ObjectNode getData(String surveyUnitId) {
        return null;
    }

    @Override
    public void saveData(String surveyUnitId, ObjectNode dataValue) {

    }

    @Override
    public void updateCollectedData(String surveyUnitId, ObjectNode collectedData) {
        checkUpdateCollectedData = true;
    }
}
