package fr.insee.queen.application.interrogation.dto.input;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.json.JsonValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@Schema(name = "InterrogationUpdate")
public record InterrogationUpdateInput(
        @Schema(ref = SchemaType.Names.PERSONALIZATION)
        @JsonValid(SchemaType.PERSONALIZATION)
        ArrayNode personalization,
        @Schema(ref = SchemaType.Names.DATA)
        @JsonValid(SchemaType.DATA)
        ObjectNode data,
        ObjectNode comment,
        @Valid
        StateDataForInterrogationUpdateInput stateData) {

    public static Interrogation toModel(String interrogationId, InterrogationUpdateInput interrogation) {
        ArrayNode personalization = interrogation.personalization();
        ObjectNode comment = interrogation.comment();
        ObjectNode data = interrogation.data();
        return Interrogation.createForUpdate(interrogationId, null, personalization, comment, data,
                StateDataForInterrogationUpdateInput.toModel(interrogation.stateData()));
    }
}
