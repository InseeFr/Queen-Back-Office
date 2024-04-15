package fr.insee.queen.domain.surveyunittempzone.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.UUID;

public record SurveyUnitTempZone(
        UUID id,
        String surveyUnitId,
        String userId,
        Long date,
        ObjectNode surveyUnit) {
}
