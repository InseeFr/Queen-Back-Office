package fr.insee.queen.application.surveyunittempzone.dto.output;

import com.fasterxml.jackson.annotation.JsonRawValue;
import fr.insee.queen.domain.surveyunittempzone.model.SurveyUnitTempZone;

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
