package fr.insee.queen.domain.surveyunit.model;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public record SurveyUnit(
        String id,
        String campaignId,
        String questionnaireId,
        ArrayNode personalization,
        ObjectNode data,
        ObjectNode comment,
        StateData stateData) {
    public static SurveyUnit createForUpdate(String surveyUnitId, ArrayNode personalization, ObjectNode comment, ObjectNode data, StateData stateData) {
        return new SurveyUnit(surveyUnitId, null, null, personalization, data, comment, stateData);
    }
}
