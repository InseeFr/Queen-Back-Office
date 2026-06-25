package fr.insee.queen.domain.common.model;

import fr.insee.queen.domain.interrogation.model.StateDataType;

public enum CollectMode {
    CAPI {
        public boolean blocksTransitionFrom(StateDataType state) { return false; }
    },
    CAWI {
        public boolean blocksTransitionFrom(StateDataType state) {
            return StateDataType.VALIDATED.equals(state) || StateDataType.EXTRACTED.equals(state);
        }
    };

    public abstract boolean blocksTransitionFrom(StateDataType state);
}
