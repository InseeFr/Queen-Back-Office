package fr.insee.queen.domain.surveyunittempzone.model;

import java.util.UUID;

public record SurveyUnitTempZone(
        UUID id,
        String surveyUnitId,
        String userId,
        Long date,
        String surveyUnit) {
}
