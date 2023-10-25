package fr.insee.queen.api.dto.integration;


import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.queen.api.dto.IntegrationStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IntegrationResultUnitDto(
		String id,
		IntegrationStatus status,
		String cause){}
