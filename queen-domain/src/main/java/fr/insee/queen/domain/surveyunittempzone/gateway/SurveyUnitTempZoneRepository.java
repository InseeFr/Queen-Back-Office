package fr.insee.queen.domain.surveyunittempzone.gateway;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunittempzone.model.SurveyUnitTempZone;

import java.util.List;

/**
 * Repository to handle survey units in temp zone
 *
 * @author Laurent Caouissin
 */
public interface SurveyUnitTempZoneRepository {

    void delete(String surveyUnitId);

    List<SurveyUnitTempZone> getAllSurveyUnits();

    void save(String surveyUnitId, String userId, Long date, ObjectNode surveyUnit);
}
