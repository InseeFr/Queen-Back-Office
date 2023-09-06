package fr.insee.queen.api.dto.nomenclature;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NomenclatureDto(
	String id,
	String label,
	String value){}
