package fr.insee.queen.api.dto.integration;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationResultUnitDto {
	@JsonProperty
	private String id;
	@JsonProperty
	private IntegrationStatus status;
	@JsonProperty
	private String cause;
}
