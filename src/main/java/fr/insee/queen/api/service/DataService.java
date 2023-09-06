package fr.insee.queen.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.dto.data.DataDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.DataRepository;
import fr.insee.queen.api.repository.SurveyUnitCreateUpdateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class DataService {
    private final DataRepository dataRepository;
	private final SurveyUnitCreateUpdateRepository surveyUnitCreateUpdateRepository;

	public DataDto getData(String surveyUnitId) {
		return dataRepository.findBySurveyUnitId(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Data for survey unit id %s not found", surveyUnitId)));
	}

	public void save(Data comment) {
		dataRepository.save(comment);
	}

	public void updateData(String surveyUnitId, JsonNode dataValue) {
		surveyUnitCreateUpdateRepository.updateSurveyUnitData(surveyUnitId, dataValue);
	}
}
