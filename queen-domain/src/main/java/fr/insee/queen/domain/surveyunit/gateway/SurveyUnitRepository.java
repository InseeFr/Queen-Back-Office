package fr.insee.queen.domain.surveyunit.gateway;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitDepositProof;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitState;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;

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
    void savePersonalization(String surveyUnitId, String personalization);

    /**
     * Save comment of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @param comment comment value
     */
    void saveComment(String surveyUnitId, String comment);

    /**
     * Save data of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @param data data value
     */
    void saveData(String surveyUnitId, String data);

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
    Optional<String> findComment(String surveyUnitId);

    /**
     * Find the data of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return the data value
     */
    Optional<String> findData(String surveyUnitId);

    /**
     * Find the personalization of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return the personalization value
     */
    Optional<String> findPersonalization(String surveyUnitId);

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
    void updateInfos(SurveyUnit surveyUnit);

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
}
