package fr.insee.queen.application.surveyunit.dto.input;

import fr.insee.queen.domain.surveyunit.model.StateData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "StateDataUpdate")
public record StateDataInput(
        @NotNull
        StateDataTypeInput state,
        @NotBlank
        String currentPage) {

    public static StateData toModel(StateDataInput stateDataInputDto) {
        if (stateDataInputDto == null) {
            return null;
        }
        return new StateData(stateDataInputDto.state().getStateDataType(), null, stateDataInputDto.currentPage);
    }
}

