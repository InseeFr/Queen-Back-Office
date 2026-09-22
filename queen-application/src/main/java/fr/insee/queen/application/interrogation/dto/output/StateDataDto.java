package fr.insee.queen.application.interrogation.dto.output;

import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "StateData")
public record StateDataDto(
        StateDataType state,
        Long date,
        String currentPage,
        List<LeafStateOutput> leafStates) {
    public static StateDataDto fromModel(StateData stateData) {
        if (stateData == null) {
            return null;
        }

        if(stateData.leafStates() == null){
            return new StateDataDto(stateData.state(), stateData.date(), stateData.currentPage(), null);
        }

        List<LeafStateOutput> leafStates = stateData.leafStates()
                .stream()
                .map(leafState -> new LeafStateOutput(leafState.state(), leafState.date()))
                .toList();
        return new StateDataDto(stateData.state(), stateData.date(), stateData.currentPage(), leafStates);

    }
}

