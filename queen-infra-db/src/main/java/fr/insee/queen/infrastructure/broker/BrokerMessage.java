package fr.insee.queen.infrastructure.broker;

import java.util.List;

public record BrokerMessage(String type, Payload payload) {

    public record Payload(String interrogationId, List<LeafState> leafStates) {
        public boolean hasLeafStates() {
            return leafStates != null && !leafStates.isEmpty();
        }
    }

    public record LeafState(Long date, String state) {
    }
}