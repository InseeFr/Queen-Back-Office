package fr.insee.queen.api.repository;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.dto.surveyunit.SurveyUnitResponseDto;

public interface SimpleApiRepository {

    void updateSurveyUnitData(String id, JsonNode data);
    void updateSurveyUnitComment(String id, JsonNode comment);
    void updateSurveyUnitPersonalization(String id, JsonNode personalization);
    void updateSurveyUnitStateDate(String id, JsonNode stateData);

    String getCampaignIdFromSuId(String id);
    boolean idCampaignIsPresent(String id);

    void createSurveyUnit(String campaignId, SurveyUnitResponseDto surveyUnitResponseDto);

}
