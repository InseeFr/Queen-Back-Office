package fr.insee.queen.application.surveyunit.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(name = "SurveyUnitDataStateDataUpdate")
public record SurveyUnitDataStateDataUpdateInput(
        @NotNull
        ObjectNode data,
        @Valid
        @NotNull
        StateDataInput stateData) {
}