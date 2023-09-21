package fr.insee.queen.api.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NomenclatureInputDto(
		@NotBlank
		String id,
		@NotBlank
		String label,
		@NotNull
		ArrayNode value){
}
