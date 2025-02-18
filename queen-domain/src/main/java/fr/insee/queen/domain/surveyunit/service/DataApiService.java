package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class DataApiService implements DataService {
    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    public ObjectNode getData(String surveyUnitId) {
        return surveyUnitRepository
                .findData(surveyUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Data not found for survey unit %s", surveyUnitId)));
    }

    @Override
    @Transactional
    public void saveData(String surveyUnitId, ObjectNode dataValue) {
        surveyUnitRepository.saveData(surveyUnitId, dataValue);
    }

    @Override
    @Transactional
    public void updateCollectedData(String surveyUnitId, ObjectNode collectedData) {
        surveyUnitRepository.updateCollectedData(surveyUnitId, collectedData);
    }

    @Override
    @Transactional
    public void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp) {
        surveyUnitRepository.cleanExtractedData(campaignId, startTimestamp, endTimestamp);
    }


}
