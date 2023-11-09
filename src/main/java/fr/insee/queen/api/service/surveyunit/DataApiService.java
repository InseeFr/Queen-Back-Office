package fr.insee.queen.api.service.surveyunit;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.service.gateway.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class DataApiService implements DataService {
	private final SurveyUnitRepository surveyUnitRepository;

	public String getData(String surveyUnitId) {
		return surveyUnitRepository
				.findData(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Data not found for survey unit %s", surveyUnitId)));
	}

	public void updateData(String surveyUnitId, JsonNode dataValue) {
		surveyUnitRepository.updateData(surveyUnitId, dataValue.toString());
	}
}
