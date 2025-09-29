package fr.insee.queen.infrastructure.db.data.repository.jpa;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.data.entity.common.DataDB;

import java.util.Optional;

/**
 * JPA repository to handle interrogation's data response for a questionnaire
 */
public interface DataRepository {

    /**
     * Find the data of an interrogation
     *
     * @param interrogationId interrogation id
     * @return an optional of the data (json format)
     */
    Optional<ObjectNode> findData(String interrogationId);

    /**
     * Update data for an interrogation
     *
     * @param interrogationId interrogation id
     * @param data json data to set
     * @return number of updated rows
     */
    int updateData(String interrogationId, ObjectNode data);

    /**
     * Update data for an interrogation
     *
     * @param interrogationId interrogation id
     * @param collectedUpdateData partial collected data to set on current collected data
     */
    void updateCollectedData(String interrogationId, ObjectNode collectedUpdateData);

    /**
     * Save data for an interrogation
     *
     * @param data data to save
     * @return saved entity
     */
    DataDB save(DataDB data);

    /**
     * clean all extracted data for a campaign
     *
     * @param campaignId campaign id
     * @param startTimestamp start timestamp
     * @param endTimestamp end timestamp
     */
    void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp);
}
