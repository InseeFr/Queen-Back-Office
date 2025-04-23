package fr.insee.queen.domain.surveyunit.gateway;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunit.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository to handle survey units
 */
public interface SurveyUnitRepository {
    /**
     * Find summary of survey unit by id
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnitSummary} survey unit summary
     */
    Optional<SurveyUnitSummary> findSummaryById(String surveyUnitId);

    /**
     * Find all survey unit summary by campaign
     *
     * @param campaignId campaign id
     * @return List of {@link SurveyUnitSummary} survey unit summary
     */
    List<SurveyUnitSummary> findAllSummaryByCampaignId(String campaignId);

    /**
     * Find all survey unit summary by survey unit ids
     *
     * @param surveyUnitIds survey units we want to retrieve
     * @return List of {@link SurveyUnitSummary} survey unit summary
     */
    List<SurveyUnitSummary> findAllSummaryByIdIn(List<String> surveyUnitIds);

    /**
     * Retrieve a survey unit with all details
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnit} survey unit
     */
    Optional<SurveyUnit> find(String surveyUnitId);

    /**
     * Retrieve a survey unit with campaign and state data linked (used for deposit proof)
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnitDepositProof} survey unit
     */
    Optional<SurveyUnitDepositProof> findWithCampaignAndStateById(String surveyUnitId);

    /**
     * Find all survey unit ids
     *
     * @return List of survey unit ids
     */
    Optional<List<String>> findAllIds();

    /**
     *
     * @param campaignId campaign id
     * @param stateDataType state data type to filter
     * @return pages of survey units with states
     */
    List<SurveyUnitState> findAllByState(String campaignId, StateDataType stateDataType);

    /**
     * Find survey units with state linked by ids
     *
     * @param surveyUnitIds survey unit ids
     * @return List of {@link SurveyUnitState} survey units
     */
    List<SurveyUnitState> findAllWithStateByIdIn(List<String> surveyUnitIds);

    /**
     * Delete survey units linked to a campaign
     *
     * @param campaignId campaign id
     */
    void deleteSurveyUnits(String campaignId);

    /**
     * Delete survey unit (with data/paradatas/state-data/comment/survey unit temp zone)
     *
     * @param surveyUnitId survey unit id
     */
    void delete(String surveyUnitId);

    /**
     * Create survey unit
     *
     * @param surveyUnit survey unit to create
     */
    void create(SurveyUnit surveyUnit);

    /**
     * Update personalization of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @param personalization personalization value
     */
    void savePersonalization(String surveyUnitId, ArrayNode personalization);

    /**
     * Save comment of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @param comment comment value
     */
    void saveComment(String surveyUnitId, ObjectNode comment);

    /**
     * Save data of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @param data data value
     */
    void saveData(String surveyUnitId, ObjectNode data);

    /**
     * Save partial collected data for a survey unit
     * @param surveyUnitId survey unit id
     * @param partialCollectedDataNode partial data value
     */
    void updateCollectedData(String surveyUnitId, ObjectNode partialCollectedDataNode);

    /**
     * Find the comment of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return the comment value
     */
    Optional<ObjectNode> findComment(String surveyUnitId);

    /**
     * Find the data of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return the data value
     */
    Optional<ObjectNode> findData(String surveyUnitId);

    /**
     * Get the personalization of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return the personalization value
     */
    SurveyUnitPersonalization getSurveyUnitPersonalization(String surveyUnitId);

    /**
     * Get the personalization of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return the personalization value
     */
    Optional<ArrayNode> findPersonalization(String surveyUnitId);


    /**
     * Check if survey unit exists
     * @param surveyUnitId survey unit id
     * @return true if exists, false otherwise
     */
    boolean exists(String surveyUnitId);

    /**
     * Update survey unit infos (data/state-data/comment/personalization)
     * @param surveyUnit survey unit to update
     */
    void update(SurveyUnit surveyUnit);

    /**
     *
     * @param surveyUnitIds list of survey unit ids to find
     * @return List of {@link SurveyUnit} survey units found
     */
    List<SurveyUnit> find(List<String> surveyUnitIds);

    /**
     * Return all full survey units (don't use this instead you're really sure !)
     *
     * @return List of {@link SurveyUnit} all survey units
     */
    List<SurveyUnit> findAll();

    /**
     * clear all extracted data for a campaign between 2 timestamps
     *
     * @param campaignId campaign id
     * @param startTimestamp timestamp start
     * @param endTimestamp timestamp end
     */
    void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp);
}
