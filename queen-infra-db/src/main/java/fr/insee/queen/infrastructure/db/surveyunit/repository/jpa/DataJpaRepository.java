package fr.insee.queen.infrastructure.db.surveyunit.repository.jpa;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.surveyunit.entity.DataDB;

import java.util.Optional;

/**
 * JPA repository to handle survey unit's data response for a questionnaire
 */
public interface DataJpaRepository {
    /**
     * Delete all survey units data for a campaign
     *
     * @param campaignId campaign id
     */
    void deleteDatas(String campaignId);

    /**
     * Update data for a survey unit
     *
     * @param surveyUnitId survey unit id
     * @param data json data to set
     * @return number of updated rows
     */
    int updateData(String surveyUnitId, ObjectNode data);

    /**
     * Find the data of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return an optional of the data (json format)
     */
    Optional<ObjectNode> findData(String surveyUnitId);

    /**
     * Delete data of a survey unit
     * @param surveyUnitId survey unit id
     */
    void deleteBySurveyUnitId(String surveyUnitId);

    /**
     * Update data for a survey unit
     *
     * @param surveyUnitId survey unit id
     * @param collectedUpdateData partial collected data to set on current collected data
     */
    void updateCollectedData(String surveyUnitId, ObjectNode collectedUpdateData);

    /**
     * Save data for a survey unit
     *
     * @param data data to save
     * @return saved entity
     */
    DataDB save(DataDB data);
}
