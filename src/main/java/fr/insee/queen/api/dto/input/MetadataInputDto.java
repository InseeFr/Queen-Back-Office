package fr.insee.queen.api.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;

public record MetadataInputDto(@NotNull ObjectNode value) {
}
