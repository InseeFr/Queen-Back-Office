package fr.insee.queen.domain.surveyunit.model;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public record SurveyUnitCommand(
        String id,
        String questionnaireId,
        ArrayNode personalization,
        ObjectNode data,
        String correlationId) {
}
