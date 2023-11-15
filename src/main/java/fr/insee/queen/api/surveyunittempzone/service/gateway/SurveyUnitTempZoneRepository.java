package fr.insee.queen.api.surveyunittempzone.service.gateway;

import fr.insee.queen.api.surveyunittempzone.service.model.SurveyUnitTempZone;

import java.util.List;

/**
 * Repository to handle survey units in temp zone
 *
 * @author Laurent Caouissin
 */
public interface SurveyUnitTempZoneRepository {

    void delete(String surveyUnitId);

    List<SurveyUnitTempZone> getAllSurveyUnits();

    void save(String surveyUnitId, String userId, Long date, String surveyUnit);
}
