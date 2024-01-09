package fr.insee.queen.application.campaign.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;

/**
 * Data used for metadata creation
 * @param value json value of metadata
 */
public record MetadataCreationData(@NotNull ObjectNode value) {
}
