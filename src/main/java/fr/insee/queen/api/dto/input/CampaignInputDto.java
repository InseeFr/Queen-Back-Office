package fr.insee.queen.api.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.queen.api.controller.validation.IdValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignInputDto(
		@IdValid
		String id,
		@NotBlank
		String label,
		@NotEmpty
		Set<String> questionnaireIds,
		@Valid
		MetadataInputDto metadata){
}