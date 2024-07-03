package fr.insee.queen.application.surveyunit.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.application.campaign.component.MetadataConverter;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "SurveyUnitMetadata")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SurveyUnitMetadataDto(
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
        public static SurveyUnitMetadataDto fromModel(SurveyUnitMetadata surveyUnitMetadata, MetadataConverter converter) {
                MetadataDto questionnaireMetadata = converter.convert(surveyUnitMetadata.metadata());
                return new SurveyUnitMetadataDto(
                        questionnaireMetadata.context(),
                        surveyUnitMetadata.surveyUnitPersonalization().personalization(),
                        questionnaireMetadata.label(),
                        questionnaireMetadata.objectives(),
                        questionnaireMetadata.variables(),
                        questionnaireMetadata.logos());
        }
}
