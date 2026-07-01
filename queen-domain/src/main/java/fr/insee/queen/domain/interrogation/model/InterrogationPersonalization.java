package fr.insee.queen.domain.interrogation.model;


import tools.jackson.databind.node.ArrayNode;

public record InterrogationPersonalization(
        String interrogationId,
        String groupId,
        String questionnaireId,
        ArrayNode personalization) {
}
