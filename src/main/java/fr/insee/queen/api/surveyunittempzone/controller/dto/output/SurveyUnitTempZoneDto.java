package fr.insee.queen.api.surveyunittempzone.controller.dto.output;

import com.fasterxml.jackson.annotation.JsonRawValue;
import fr.insee.queen.api.surveyunittempzone.service.model.SurveyUnitTempZone;

import java.util.UUID;

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
