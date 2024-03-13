package fr.insee.queen.domain.surveyunit.service.dummy;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.domain.surveyunit.service.DataService;
import lombok.Getter;

public class DataFakeService implements DataService {
    @Getter
    private JsonNode dataSaved = null;

    @Override
    public String getData(String surveyUnitId) {
        return null;
    }

    @Override
    public void updateData(String surveyUnitId, JsonNode dataValue) {
        dataSaved = dataValue;
    }
}
