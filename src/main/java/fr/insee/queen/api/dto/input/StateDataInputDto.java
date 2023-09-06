package fr.insee.queen.api.dto.input;

import fr.insee.queen.api.domain.StateDataType;

public record StateDataInputDto(
        StateDataType state,
        Long date,
        String currentPage){
}

