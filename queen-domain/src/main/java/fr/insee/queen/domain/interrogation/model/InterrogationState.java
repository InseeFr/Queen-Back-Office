package fr.insee.queen.domain.interrogation.model;

public record InterrogationState(
        String id,
        String surveyUnitId,
        String questionnaireId,
        String campaignId,
        StateData stateData) {
}
