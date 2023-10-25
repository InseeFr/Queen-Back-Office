package fr.insee.queen.api.dto.statedata;

public record StateDataDto(
	StateDataType state,
	Long date,
	String currentPage){
	public static StateDataDto createEmptyStateData() {
		return new StateDataDto(null, null, null);
	}
}

