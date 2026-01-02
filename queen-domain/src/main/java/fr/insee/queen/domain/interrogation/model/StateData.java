package fr.insee.queen.domain.interrogation.model;

import java.util.List;

public record StateData(
        StateDataType state,
        Long date,
        String currentPage,
        List<LeafState> leafStates) {

    public StateData(StateDataType state, Long date, String currentPage) {
        this(state, date, currentPage, List.of());
    }
}