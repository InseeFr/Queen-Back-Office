package fr.insee.queen.api.surveyunittempzone.service.model;

import java.util.UUID;

public record SurveyUnitTempZone(
        UUID id,
        String surveyUnitId,
        String userId,
        Long date,
        String surveyUnit) {
}
