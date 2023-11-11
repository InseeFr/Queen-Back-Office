package fr.insee.queen.api.campaign.controller.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;

public record MetadataCreationData(@NotNull ObjectNode value) {
}
