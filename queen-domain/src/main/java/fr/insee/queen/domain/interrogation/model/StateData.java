package fr.insee.queen.domain.interrogation.model;

/**
 * TODO Ajouter leafState
 */
public record StateData(
        StateDataType state,
        Long date,
        String currentPage) {
}