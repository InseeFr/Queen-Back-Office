package fr.insee.queen.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.domain.Personalization;
import fr.insee.queen.api.dto.personalization.PersonalizationDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.PersonalizationRepository;
import fr.insee.queen.api.repository.SurveyUnitCreateUpdateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersonalizationService {

	private final PersonalizationRepository personalizationRepository;
	private final SurveyUnitCreateUpdateRepository surveyUnitCreateUpdateRepository;

	public void save(Personalization personalization) {
		personalizationRepository.save(personalization);
	}

	public PersonalizationDto getPersonalization(String surveyUnitId){
		return personalizationRepository.findBySurveyUnitId(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Personalization for survey unit id %s was not found", surveyUnitId)));
	}
	
	public void updatePersonalization(String surveyUnitId, JsonNode persValue) {
		surveyUnitCreateUpdateRepository.updateSurveyUnitPersonalization(surveyUnitId, persValue);
	}
}
