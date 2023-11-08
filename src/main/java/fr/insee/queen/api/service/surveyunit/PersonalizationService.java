package fr.insee.queen.api.service.surveyunit;

import com.fasterxml.jackson.databind.JsonNode;

public interface PersonalizationService {
	String getPersonalization(String surveyUnitId);
	void updatePersonalization(String surveyUnitId, JsonNode commentValue);
}
