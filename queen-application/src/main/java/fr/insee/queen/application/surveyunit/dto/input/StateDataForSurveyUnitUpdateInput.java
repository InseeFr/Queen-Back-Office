package fr.insee.queen.application.surveyunit.dto.input;

import fr.insee.queen.domain.surveyunit.model.StateData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "StateDataForSurveyUnitUpdate")
public record StateDataForSurveyUnitUpdateInput(
        StateDataTypeInput state,
        @NotNull
        Long date,
        @NotBlank
        String currentPage) {

    public static StateData toModel(StateDataForSurveyUnitUpdateInput stateDataInputDto) {
        if (stateDataInputDto == null) {
            return null;
        }

        // TODO when retrocompatibility isnot needed anymore, make the state @notNull again and delete this test
        // used to keep compatibility with old versions where a state could be null.
        // Now, if the state is null, the state data is considered null
        if(stateDataInputDto.state() == null) {
            return null;
        }
        return new StateData(stateDataInputDto.state().getStateDataType(), stateDataInputDto.date(), stateDataInputDto.currentPage);
    }
}

