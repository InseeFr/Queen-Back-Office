package fr.insee.queen.application.campaign.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Data used for metadata creation
 * @param value json value of metadata
 */
@Schema(name = "MetadataCreation")
public record MetadataCreationData(@NotNull ObjectNode value) {
}
