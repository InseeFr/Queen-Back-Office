package fr.insee.queen.api.service.surveyunit;

import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class StateDataService {
	private final SurveyUnitRepository surveyUnitRepository;

	public StateDataDto getStateData(String surveyUnitId) {
		return surveyUnitRepository.findStateDataBySurveyUnitId(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s not found", surveyUnitId)));
	}
	
	public void updateStateData(String surveyUnitId, StateDataDto stateData) {
		surveyUnitRepository.updateStateData(surveyUnitId, stateData);
	}
}
