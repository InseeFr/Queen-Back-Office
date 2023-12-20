package fr.insee.queen.api.surveyunittempzone.service;

import fr.insee.queen.api.surveyunittempzone.service.model.SurveyUnitTempZone;

import java.util.List;

public interface SurveyUnitTempZoneService {
    void saveSurveyUnitToTempZone(String surveyUnitId, String userId, String surveyUnitData);

    List<SurveyUnitTempZone> getAllSurveyUnitTempZone();
}
