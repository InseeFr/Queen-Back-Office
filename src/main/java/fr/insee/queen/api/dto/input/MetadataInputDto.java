package fr.insee.queen.api.dto.input;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;

public record MetadataInputDto(@NotNull JsonNode value) {
}
