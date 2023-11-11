package fr.insee.queen.api.service.surveyunit;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitTempZoneDto;

import java.util.List;

public interface SurveyUnitTempZoneService {
	void saveSurveyUnitToTempZone(String surveyUnitId, String userId, JsonNode surveyUnit);
	List<SurveyUnitTempZoneDto> getAllSurveyUnitTempZoneDto();
}
