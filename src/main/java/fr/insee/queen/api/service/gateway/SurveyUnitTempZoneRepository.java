package fr.insee.queen.api.service.gateway;

import fr.insee.queen.api.dto.surveyunit.SurveyUnitTempZoneDto;

import java.util.List;
import java.util.UUID;

/**
* SurveyUnitTempZone is the repository using to save surveyUnit with probleme in DB
*
* @author Laurent Caouissin
*
*/
public interface SurveyUnitTempZoneRepository {

    void delete(String surveyUnitId);

    List<SurveyUnitTempZoneDto> getAllSurveyUnits();

	void save(UUID id, String surveyUnitId, String userId, Long date, String surveyUnit);
}
