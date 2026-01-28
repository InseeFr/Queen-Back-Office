package fr.insee.queen.jms.model;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    public static Interrogation toModel(InterrogationAsyncInput interrogation, String campaignId) {
        return new Interrogation(interrogation.id,
                interrogation.surveyUnitId(),
                campaignId,
                interrogation.questionnaireId(),
                interrogation.personalization(),
                interrogation.data(),
                null,
                null,
                interrogation.correlationId,
                null);
    }
}
