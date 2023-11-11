package fr.insee.queen.api.surveyunit.controller.dto.output;

import fr.insee.queen.api.depositproof.service.model.StateDataType;
import fr.insee.queen.api.surveyunit.service.model.StateData;

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

