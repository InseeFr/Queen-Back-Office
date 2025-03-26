package fr.insee.queen.domain.paradata.gateway;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;

public interface ParadataEventRepository {
    /**
     * Create paradata for a interrogation
     *
     * @param paradataValue paradata value (json format)
     * @param interrogationSummary interrogation summary
     */
    void createParadataEvent(InterrogationSummary interrogationSummary, ObjectNode paradataValue);
}
