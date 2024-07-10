package fr.insee.queen.domain.paradata.gateway;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.UUID;

public interface ParadataEventRepository {
    /**
     * Create paradata for a survey unit
     *
     * @param id paradata id
     * @param paradataValue paradata value (json format)
     * @param surveyUnitId survey unit id
     */
    void createParadataEvent(UUID id, ObjectNode paradataValue, String surveyUnitId);
}
