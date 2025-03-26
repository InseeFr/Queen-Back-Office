package fr.insee.queen.domain.interrogation.model;

public record InterrogationState(
        String id,
        String questionnaireId,
        String campaignId,
        StateData stateData) {
}
