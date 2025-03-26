package fr.insee.queen.domain.interrogation.model;


import com.fasterxml.jackson.databind.node.ArrayNode;

public record InterrogationPersonalization(
        String interrogationId,
        String questionnaireId,
        ArrayNode personalization) {
}
