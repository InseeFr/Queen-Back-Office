package fr.insee.queen.application.surveyunittempzone.dto.output;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.surveyunittempzone.model.SurveyUnitTempZone;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "SurveyUnitTempZone")
public record SurveyUnitTempZoneDto(
        UUID id,
        String surveyUnitId,
        String userId,
        Long date,
        @Schema(ref = SchemaType.Names.SURVEY_UNIT_TEMP_ZONE)
        ObjectNode surveyUnit) {

    public static SurveyUnitTempZoneDto fromModel(SurveyUnitTempZone surveyUnit) {
        return new SurveyUnitTempZoneDto(surveyUnit.id(), surveyUnit.surveyUnitId(), surveyUnit.userId(), surveyUnit.date(), surveyUnit.surveyUnit());
    }
}
