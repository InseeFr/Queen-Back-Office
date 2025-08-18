package fr.insee.queen.domain.interrogation.model;

public record StateData(
        StateDataType state,
        Long date,
        String currentPage) {
}