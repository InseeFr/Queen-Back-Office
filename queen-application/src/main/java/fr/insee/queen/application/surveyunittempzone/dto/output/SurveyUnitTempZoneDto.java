package fr.insee.queen.application.surveyunittempzone.dto.output;

import com.fasterxml.jackson.annotation.JsonRawValue;
import fr.insee.queen.domain.surveyunittempzone.model.SurveyUnitTempZone;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "SurveyUnitTempZone")
public record SurveyUnitTempZoneDto(
        UUID id,
        String surveyUnitId,
        String userId,
        Long date,
        @JsonRawValue
        String surveyUnit) {

    public static SurveyUnitTempZoneDto fromModel(SurveyUnitTempZone surveyUnit) {
        return new SurveyUnitTempZoneDto(surveyUnit.id(), surveyUnit.surveyUnitId(), surveyUnit.userId(), surveyUnit.date(), surveyUnit.surveyUnit());
    }
}
