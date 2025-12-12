package fr.insee.queen.domain.interrogation.gateway;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository to handle interrogations
 */
public interface InterrogationRepository {
    /**
     * Find summary of interrogation by id
     *
     * @param interrogationId interrogation id
     * @return {@link InterrogationSummary} interrogation summary
     */
    Optional<InterrogationSummary> findSummaryById(String interrogationId);

    /**
     * Find all interrogation summary by campaign
     *
     * @param campaignId campaign id
     * @return List of {@link InterrogationSummary} interrogation summary
     */
    List<InterrogationSummary> findAllSummaryByCampaignId(String campaignId);

    /**
     * Find all interrogation summary by survey-unit
     *
     * @param surveyUnitId
     * @return List of {@link InterrogationSummary} interrogation summary
     */
    List<InterrogationSummary> findAllSummaryBySurveyUnitId(String surveyUnitId);

    /**
     * Find all interrogation summary by interrogation ids
     *
     * @param interrogationIds interrogations we want to retrieve
     * @return List of {@link InterrogationSummary} interrogation summary
     */
    List<InterrogationSummary> findAllSummaryByIdIn(List<String> interrogationIds);

    /**
     * Retrieve an interrogation with all details
     *
     * @param interrogationId interrogation id
     * @return {@link Interrogation} interrogation
     */
    Optional<Interrogation> find(String interrogationId);

    /**
     * Retrieve an interrogation with campaign and state data linked (used for deposit proof)
     *
     * @param interrogationId interrogation id
     * @return {@link InterrogationDepositProof} interrogation
     */
    Optional<InterrogationDepositProof> findWithCampaignAndStateById(String interrogationId);

    /**
     * Find all interrogation ids
     *
     * @return List of interrogation ids
     */
    Optional<List<String>> findAllIds();

    /**
     *
     * @param campaignId campaign id
     * @param stateDataType state data type to filter
     * @return pages of interrogations with states
     */
    List<InterrogationState> findAllByState(String campaignId, StateDataType stateDataType);

    /**
     * Find all interrogations with full details by state
     *
     * @param stateDataType state data type to filter
     * @return List of {@link Interrogation} interrogations
     */
    List<Interrogation> findAllByState(StateDataType stateDataType);

    /**
     * Find interrogations with state linked by ids
     *
     * @param interrogationIds interrogation ids
     * @return List of {@link InterrogationState} interrogations
     */
    List<InterrogationState> findAllWithStateByIdIn(List<String> interrogationIds);

    /**
     * Delete interrogations linked to a campaign
     *
     * @param campaignId campaign id
     */
    void deleteInterrogations(String campaignId);

    /**
     * Delete interrogation (with data/paradatas/state-data/comment/interrogation temp zone)
     *
     * @param interrogationId interrogation id
     */
    void delete(String interrogationId);

    /**
     * Create interrogation
     *
     * @param interrogation interrogation to create
     */
    void create(Interrogation interrogation);

    /**
     * Update personalization of an interrogation
     *
     * @param interrogationId interrogation id
     * @param personalization personalization value
     */
    void savePersonalization(String interrogationId, ArrayNode personalization);

    /**
     * Save comment of an interrogation
     *
     * @param interrogationId interrogation id
     * @param comment comment value
     */
    void saveComment(String interrogationId, ObjectNode comment);

    /**
     * Save data of an interrogation
     *
     * @param interrogationId interrogation id
     * @param data data value
     */
    void saveData(String interrogationId, ObjectNode data);

    /**
     * Save partial collected data for an interrogation
     * @param interrogationId interrogation id
     * @param partialCollectedDataNode partial data value
     */
    void updateCollectedData(String interrogationId, ObjectNode partialCollectedDataNode);

    /**
     * Find the comment of an interrogation
     *
     * @param interrogationId interrogation id
     * @return the comment value
     */
    Optional<ObjectNode> findComment(String interrogationId);

    /**
     * Find the data of an interrogation
     *
     * @param interrogationId interrogation id
     * @return the data value
     */
    Optional<ObjectNode> findData(String interrogationId);

    /**
     * Get the personalization of an interrogation
     *
     * @param interrogationId interrogation id
     * @return the personalization value
     */
    InterrogationPersonalization getInterrogationPersonalization(String interrogationId);

    /**
     * Get the personalization of an interrogation
     *
     * @param interrogationId interrogation id
     * @return the personalization value
     */
    Optional<ArrayNode> findPersonalization(String interrogationId);


    /**
     * Check if interrogation exists
     * @param interrogationId interrogation id
     * @return true if exists, false otherwise
     */
    boolean exists(String interrogationId);

    /**
     * Update interrogation infos (data/state-data/comment/personalization)
     * @param interrogation interrogation to update
     */
    void update(Interrogation interrogation);

    /**
     *
     * @param interrogationIds list of interrogation ids to find
     * @return List of {@link Interrogation} interrogations found
     */
    List<Interrogation> find(List<String> interrogationIds);

    /**
     * Return all full interrogations (don't use this instead you're really sure !)
     *
     * @return List of {@link Interrogation} all interrogations
     */
    List<Interrogation> findAll();

    /**
     * clear all extracted data for a campaign between 2 timestamps
     *
     * @param campaignId campaign id
     * @param startTimestamp timestamp start
     * @param endTimestamp timestamp end
     */
    void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp);
}
