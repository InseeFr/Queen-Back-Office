package fr.insee.queen.domain.paradata.gateway;

import java.util.UUID;

public interface ParadataEventRepository {
    /**
     * Create paradata for a survey unit
     *
     * @param id paradata id
     * @param paradataValue paradata value (json format)
     * @param surveyUnitId survey unit id
     */
    void createParadataEvent(UUID id, String paradataValue, String surveyUnitId);
}
