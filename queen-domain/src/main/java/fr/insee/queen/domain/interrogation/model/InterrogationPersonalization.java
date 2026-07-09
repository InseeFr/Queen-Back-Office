package fr.insee.queen.domain.interrogation.model;


import tools.jackson.databind.node.ArrayNode;

public record InterrogationPersonalization(
        String interrogationId,
        String questionnaireId,
        ArrayNode personalization) {
}
