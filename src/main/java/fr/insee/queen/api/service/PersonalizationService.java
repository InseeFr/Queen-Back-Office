package fr.insee.queen.api.service;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.domain.Personalization;
import fr.insee.queen.api.domain.SurveyUnit;

public interface PersonalizationService extends BaseService<Personalization, UUID> {

	void save(Personalization personalization);
	
	public void updatePersonalization(SurveyUnit su, JsonNode personalizationValues);

	Optional<Personalization> findBySurveyUnitId(String id);
    
}
