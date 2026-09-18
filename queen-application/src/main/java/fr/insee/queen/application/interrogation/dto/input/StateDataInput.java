package fr.insee.queen.application.interrogation.dto.input;

import fr.insee.queen.domain.interrogation.model.LeafState;
import fr.insee.queen.domain.interrogation.model.StateData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "StateDataUpdate")
public record StateDataInput(
        @NotNull
        StateDataTypeInput state,
        @NotBlank
        String currentPage,
        List<LeafStateInput> leafStates) {

    public static StateData toModel(StateDataInput stateDataInputDto) {
        if (stateDataInputDto == null) {
            return null;
        }

        if(stateDataInputDto.leafStates() == null) {
            return new StateData(stateDataInputDto.state().getStateDataType(), null, stateDataInputDto.currentPage);
        }
        List<LeafState> leafStates = stateDataInputDto.leafStates()
                .stream()
                .map(leafStateInput -> new LeafState(leafStateInput.state(), leafStateInput.date()))
                .toList();
        return new StateData(stateDataInputDto.state().getStateDataType(), null, stateDataInputDto.currentPage, leafStates);
    }
}

