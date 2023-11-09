package fr.insee.queen.api.service.surveyunit;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.service.gateway.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersonalizationApiService implements PersonalizationService {
	private final SurveyUnitRepository surveyUnitRepository;

	public String getPersonalization(String surveyUnitId) {
		return surveyUnitRepository
				.findPersonalization(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Personalization not found for survey unit %s", surveyUnitId)));
	}

	public void updatePersonalization(String surveyUnitId, JsonNode personalizationValue) {
		surveyUnitRepository.updatePersonalization(surveyUnitId, personalizationValue.toString());
	}
}
