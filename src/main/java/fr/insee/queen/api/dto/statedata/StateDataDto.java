package fr.insee.queen.api.dto.statedata;

import fr.insee.queen.api.domain.StateDataType;

public record StateDataDto(
	StateDataType state,
	Long date,
	String currentPage){
}

