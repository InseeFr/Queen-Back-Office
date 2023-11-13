package fr.insee.queen.api.surveyunit.service.model;


public record SurveyUnit(
        String id,
        String campaignId,
        String questionnaireId,
        String personalization,
        String data,
        String comment,
        StateData stateData) {
    public static SurveyUnit createForUpdate(String surveyUnitId, String personalization, String comment, String data, StateData stateData) {
        return new SurveyUnit(surveyUnitId, null, null, personalization, data, comment, stateData);
    }
}
