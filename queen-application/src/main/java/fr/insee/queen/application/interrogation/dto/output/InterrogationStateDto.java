package fr.insee.queen.application.interrogation.dto.output;

import fr.insee.queen.domain.interrogation.model.InterrogationState;

public record InterrogationStateDto(
        String id,
        String surveyUnitId,
        String questionnaireId,
        String campaignId,
        StateDataDto stateData) {

    public static InterrogationStateDto fromModel(InterrogationState interrogationState) {
        return new InterrogationStateDto(interrogationState.id(),
                interrogationState.surveyUnitId(),
                interrogationState.questionnaireId(),
                interrogationState.campaignId(),
                StateDataDto.fromModel(interrogationState.stateData()));
    }
}
