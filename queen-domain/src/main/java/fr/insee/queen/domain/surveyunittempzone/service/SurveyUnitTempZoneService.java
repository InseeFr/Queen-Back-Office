package fr.insee.queen.domain.surveyunittempzone.service;

import fr.insee.queen.domain.surveyunittempzone.model.SurveyUnitTempZone;

import java.util.List;

public interface SurveyUnitTempZoneService {
    void saveSurveyUnitToTempZone(String surveyUnitId, String userId, String surveyUnitData);

    List<SurveyUnitTempZone> getAllSurveyUnitTempZone();
}
