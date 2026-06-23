package fr.insee.queen.jms.model;


import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InterrogationAsyncInput(
//      TODO to factorize
//       @IdValid
        @NotNull
        String id,
        @NotNull
        String surveyUnitId,
        @NotNull
        String questionnaireId,
        ArrayNode personalization,
        @NotNull
        ObjectNode data,
        @NotNull
        UUID correlationId) {

    public static Interrogation toModel(InterrogationAsyncInput interrogation, String groupId) {
        return new Interrogation(interrogation.id,
                interrogation.surveyUnitId(),
                groupId,
                interrogation.questionnaireId(),
                interrogation.personalization(),
                interrogation.data(),
                null,
                interrogation.correlationId);
    }
}
