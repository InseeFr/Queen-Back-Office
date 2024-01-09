package fr.insee.queen.application.surveyunit.dto.output;

import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.StateDataType;

public record StateDataDto(
        StateDataType state,
        Long date,
        String currentPage) {
    public static StateDataDto fromModel(StateData stateData) {
        if (stateData == null) {
            return null;
        }
        return new StateDataDto(stateData.state(), stateData.date(), stateData.currentPage());
    }
}

