package fr.insee.queen.api.surveyunit.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.surveyunit.service.gateway.SurveyUnitRepository;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
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
    public void updateData(String surveyUnitId, JsonNode dataValue) {
        surveyUnitRepository.saveData(surveyUnitId, dataValue.toString());
    }
}
