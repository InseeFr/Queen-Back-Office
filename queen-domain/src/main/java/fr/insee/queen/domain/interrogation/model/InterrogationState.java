package fr.insee.queen.domain.interrogation.model;

public record InterrogationState(
        String id,
        String surveyUnitId,
        String questionnaireId,
        String groupId,
        StateData stateData) {
}
