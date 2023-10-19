package fr.insee.queen.api.dto.input;

import fr.insee.queen.api.domain.StateDataType;
import fr.insee.queen.api.dto.statedata.StateDataDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StateDataInputDto(
        @NotNull
        StateDataType state,
        @NotNull
        Long date,
        @NotBlank
        String currentPage) {

    public static StateDataDto toModel(@NotNull StateDataInputDto stateDataInputDto) {
        return new StateDataDto(stateDataInputDto.state, stateDataInputDto.date(), stateDataInputDto.currentPage);
    }
}

