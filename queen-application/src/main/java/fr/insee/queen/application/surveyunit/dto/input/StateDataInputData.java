package fr.insee.queen.application.surveyunit.dto.input;

import fr.insee.queen.domain.surveyunit.model.StateData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StateDataInputData(
        @NotNull
        StateDataTypeData state,
        @NotNull
        Long date,
        @NotBlank
        String currentPage) {

    public static StateData toModel(StateDataInputData stateDataInputDto) {
        if (stateDataInputDto == null) {
            return null;
        }
        return new StateData(stateDataInputDto.state().getStateDataType(), stateDataInputDto.date(), stateDataInputDto.currentPage);
    }
}

