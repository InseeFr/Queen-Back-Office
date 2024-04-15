package fr.insee.queen.domain.surveyunittempzone.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunittempzone.model.SurveyUnitTempZone;

import java.util.List;

public interface SurveyUnitTempZoneService {
    void saveSurveyUnitToTempZone(String surveyUnitId, String userId, ObjectNode surveyUnitData);

    List<SurveyUnitTempZone> getAllSurveyUnitTempZone();
}
