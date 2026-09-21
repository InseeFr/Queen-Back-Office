package fr.insee.queen.infrastructure.db.interrogation.projection;

import fr.insee.queen.domain.interrogation.model.LeafState;

public record LeafStateProjection(
        String interrogationId,
        String state,
        Long date) {

    public static LeafState toModel(LeafStateProjection leafStateProjection){
        return new LeafState(leafStateProjection.state(), leafStateProjection.date());
    }
}