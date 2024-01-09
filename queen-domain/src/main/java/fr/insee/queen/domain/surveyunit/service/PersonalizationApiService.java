package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersonalizationApiService implements PersonalizationService {
    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    public String getPersonalization(String surveyUnitId) {
        return surveyUnitRepository
                .findPersonalization(surveyUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Personalization not found for survey unit %s", surveyUnitId)));
    }

    @Override
    public void updatePersonalization(String surveyUnitId, JsonNode personalizationValue) {
        surveyUnitRepository.savePersonalization(surveyUnitId, personalizationValue.toString());
    }
}
