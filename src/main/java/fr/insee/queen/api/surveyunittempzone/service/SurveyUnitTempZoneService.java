package fr.insee.queen.api.surveyunittempzone.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.surveyunittempzone.service.model.SurveyUnitTempZone;

import java.util.List;

public interface SurveyUnitTempZoneService {
    void saveSurveyUnitToTempZone(String surveyUnitId, String userId, JsonNode surveyUnit);

    List<SurveyUnitTempZone> getAllSurveyUnitTempZone();
}
