package fr.insee.queen.application.surveyunit.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SurveyUnitDataStateDataUpdateInput(
        @NotNull
        ObjectNode data,
        @Valid
        @NotNull
        StateDataInput stateData) {
}