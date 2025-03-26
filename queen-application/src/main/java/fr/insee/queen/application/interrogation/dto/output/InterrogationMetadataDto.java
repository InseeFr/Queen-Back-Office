package fr.insee.queen.application.interrogation.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.application.campaign.component.MetadataConverter;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.interrogation.model.InterrogationMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "InterrogationMetadata")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InterrogationMetadataDto(
        @NotNull
        QuestionnaireContextDto context,
        @Schema(ref = SchemaType.Names.PERSONALIZATION)
        ArrayNode personalization,
        @NotNull
        String label,
        @NotNull
        String objectives,
        List<MetadataVariableDto> variables,
        LogoDtos logos
) {
        public static InterrogationMetadataDto fromModel(InterrogationMetadata interrogationMetadata, MetadataConverter converter) {
                MetadataDto questionnaireMetadata = converter.convert(interrogationMetadata.metadata());
                return new InterrogationMetadataDto(
                        questionnaireMetadata.context(),
                        interrogationMetadata.interrogationPersonalization().personalization(),
                        questionnaireMetadata.label(),
                        questionnaireMetadata.objectives(),
                        questionnaireMetadata.variables(),
                        questionnaireMetadata.logos());
        }
}
