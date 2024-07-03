package fr.insee.queen.domain.interrogation.model;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public record InterrogationCommand(
        String id,
        String surveyUnitId,
        String questionnaireId,
        ArrayNode personalization,
        ObjectNode data,
        String correlationId) {
}
