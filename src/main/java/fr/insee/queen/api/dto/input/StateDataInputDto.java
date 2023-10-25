package fr.insee.queen.api.dto.input;

import fr.insee.queen.api.dto.statedata.StateDataDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StateDataInputDto(
        @NotNull
        StateDataTypeInputDto state,
        @NotNull
        Long date,
        @NotBlank
        String currentPage) {

    public static StateDataDto toModel(@NotNull StateDataInputDto stateDataInputDto) {
        return new StateDataDto(stateDataInputDto.state().getStateDataType(), stateDataInputDto.date(), stateDataInputDto.currentPage);
    }
}

