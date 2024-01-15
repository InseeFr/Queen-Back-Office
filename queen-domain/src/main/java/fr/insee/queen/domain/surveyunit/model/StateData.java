package fr.insee.queen.domain.surveyunit.model;

public record StateData(
        StateDataType state,
        Long date,
        String currentPage) {
}