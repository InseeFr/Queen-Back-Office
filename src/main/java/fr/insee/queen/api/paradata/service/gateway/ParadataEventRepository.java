package fr.insee.queen.api.paradata.service.gateway;

import java.util.UUID;

/**
 * ParadataEventRepository is the repository using to access to ParadataEvent table in DB
 *
 * @author Corcaud Samuel
 */
public interface ParadataEventRepository {
    void createParadataEvent(UUID id, String paradataValue, String surveyUnitId);

    void deleteParadataEvents(String campaignId);
}
