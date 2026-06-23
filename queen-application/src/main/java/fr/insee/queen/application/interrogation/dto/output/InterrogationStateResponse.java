package fr.insee.queen.application.interrogation.dto.output;

import fr.insee.queen.domain.interrogation.model.InterrogationState;

public record InterrogationStateResponse(
        String id,
        String surveyUnitId,
        String questionnaireId,
        String groupId,
        StateDataDto stateData) {

    public static InterrogationStateResponse fromModel(InterrogationState interrogationState) {
        return new InterrogationStateResponse(interrogationState.id(),
                interrogationState.surveyUnitId(),
                interrogationState.questionnaireId(),
                interrogationState.groupId(),
                StateDataDto.fromModel(interrogationState.stateData()));
    }
}
