package fr.insee.queen.api.dto.integration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class IntegrationResultDto {
	@JsonProperty
	private IntegrationResultUnitDto campaign;
	@JsonProperty
	private List<IntegrationResultUnitDto> nomenclatures;
	@JsonProperty
	private List<IntegrationResultUnitDto> questionnaireModels;
}
