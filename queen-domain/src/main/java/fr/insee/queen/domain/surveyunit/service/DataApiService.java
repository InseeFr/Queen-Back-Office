package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class DataApiService implements DataService {
    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    public String getData(String surveyUnitId) {
        return surveyUnitRepository
                .findData(surveyUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Data not found for survey unit %s", surveyUnitId)));
    }

    @Override
    public void saveData(String surveyUnitId, JsonNode dataValue) {
        surveyUnitRepository.saveData(surveyUnitId, dataValue.toString());
    }

    @Override
    public void updateCollectedData(String surveyUnitId, ObjectNode collectedData) {
        surveyUnitRepository.updateCollectedData(surveyUnitId, collectedData);
    }
}
