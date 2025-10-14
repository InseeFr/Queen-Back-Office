package fr.insee.queen.domain.interrogation.model;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public record Interrogation(
        String id,
        String surveyUnitId,
        String campaignId,
        String questionnaireId,
        ArrayNode personalization,
        ObjectNode data,
        ObjectNode comment,
        StateData stateData,
        String correlationId) {
    public static Interrogation create(String id, String surveyUnitId, ArrayNode personalization,
                                    ObjectNode comment, ObjectNode data,
                                    StateData stateData) {
        return new Interrogation(id, surveyUnitId, null,
                null, personalization, data,
                comment, stateData, null);
    }

    public static Interrogation createForUpdate(String id, String surveyUnitId, ArrayNode personalization,
                                             ObjectNode comment, ObjectNode data,
                                             StateData stateData) {
        return new Interrogation(id, surveyUnitId, null, null,
                personalization, data, comment, stateData, null);
    }
}
