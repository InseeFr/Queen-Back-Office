package fr.insee.queen.domain.surveyunit.model;

public record SurveyUnitState(
        String id,
        String questionnaireId,
        String campaignId,
        StateData stateData) {
}
