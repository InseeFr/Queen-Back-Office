package fr.insee.queen.domain.interrogation.model;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public record Interrogation(
        String id,
        String campaignId,
        String questionnaireId,
        ArrayNode personalization,
        ObjectNode data,
        ObjectNode comment,
        StateData stateData) {
    public static Interrogation createForUpdate(String interrogationId, ArrayNode personalization, ObjectNode comment, ObjectNode data, StateData stateData) {
        return new Interrogation(interrogationId, null, null, personalization, data, comment, stateData);
    }
}
