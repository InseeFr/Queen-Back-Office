package fr.insee.queen.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class DataService {
	private final SurveyUnitRepository surveyUnitRepository;

	public String getData(String surveyUnitId) {
		return surveyUnitRepository.getData(surveyUnitId);
	}

	public void updateData(String surveyUnitId, JsonNode commentValue) {
		surveyUnitRepository.updateData(surveyUnitId, commentValue.toString());
	}
}
