package fr.insee.queen.api.surveyunit.service.model;

import fr.insee.queen.api.depositproof.service.model.StateDataType;

public record StateData(
        StateDataType state,
        Long date,
        String currentPage) {
}