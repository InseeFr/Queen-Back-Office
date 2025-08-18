package fr.insee.queen.application.interrogation.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.json.JsonValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(name = "InterrogationDataStateDataUpdate")
public record InterrogationDataStateDataUpdateInput(
        @Schema(ref = SchemaType.Names.COLLECTED_DATA)
        @JsonValid(SchemaType.COLLECTED_DATA)
        ObjectNode data,
        @Valid
        @NotNull
        StateDataInput stateData) {
}