package fr.insee.queen.application.interrogation.dto.input;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.application.web.validation.json.JsonValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(name = "InterrogationCreation")
public record InterrogationCreationInput(
        @IdValid
        String id,
        @IdValid
        String surveyUnitId,
        @NotNull
        String questionnaireId,
        @Schema(ref = SchemaType.Names.PERSONALIZATION)
        @JsonValid(SchemaType.PERSONALIZATION)
        ArrayNode personalization,
        @NotNull
        @Schema(ref = SchemaType.Names.DATA)
        ObjectNode data,
        @Valid
        StateDataInput stateData) {

    public static Interrogation toModel(InterrogationCreationInput interrogation, String campaignId) {
        return new Interrogation(interrogation.id,
                interrogation.surveyUnitId(),
                campaignId,
                interrogation.questionnaireId(),
                interrogation.personalization(),
                interrogation.data(),
                StateDataInput.toModel(interrogation.stateData()),
                null);
    }
}
