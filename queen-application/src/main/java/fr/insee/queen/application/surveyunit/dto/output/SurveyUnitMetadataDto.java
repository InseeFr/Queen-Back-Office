package fr.insee.queen.application.surveyunit.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.application.campaign.component.MetadataComponentConverter;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitMetadata;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "SurveyUnitMetadata")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SurveyUnitMetadataDto(
        QuestionnaireContextDto context,
        @Schema(ref = SchemaType.Names.PERSONALIZATION)
        ArrayNode personalization,
        String label,
        String objectives,
        List<MetadataVariableDto> variables,
        LogoDtos logos
) {
        public static SurveyUnitMetadataDto fromModel(SurveyUnitMetadata surveyUnitMetadata, MetadataComponentConverter converter) {
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