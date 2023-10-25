package fr.insee.queen.api.service.surveyunit;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersonalizationService {
	private final SurveyUnitRepository surveyUnitRepository;

	public String getPersonalization(String surveyUnitId) {
		return surveyUnitRepository.getPersonalization(surveyUnitId);
	}

	public void updatePersonalization(String surveyUnitId, JsonNode commentValue) {
		surveyUnitRepository.updatePersonalization(surveyUnitId, commentValue.toString());
	}
}
