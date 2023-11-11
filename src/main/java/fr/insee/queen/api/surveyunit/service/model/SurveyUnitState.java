package fr.insee.queen.api.surveyunit.service.model;

public record SurveyUnitState(
        String id,
        String questionnaireId,
        String campaignId,
        StateData stateData) {
}
