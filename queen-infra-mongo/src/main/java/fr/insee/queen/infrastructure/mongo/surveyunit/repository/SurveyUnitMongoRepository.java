package fr.insee.queen.infrastructure.mongo.surveyunit.repository;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.mongo.surveyunit.document.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository to handle survey units in DB
 */
@Repository
public interface SurveyUnitMongoRepository extends MongoRepository<SurveyUnitDocument, String> {

    /**
     * Find summary of survey unit by id
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnitDocument} survey unit summary
     */
    @Query(value = "{ '_id' : ?0 }", fields = "{ 'questionnaire-id' : 1, 'campaign-id' : 1 }")
    Optional<SurveyUnitDocument> findSummaryById(String surveyUnitId);

    /**
     * Find all survey unit summary by campaign
     *
     * @param campaignId campaign id
     * @return List of {@link SurveyUnitDocument} survey unit summary
     */
    @Query(value = "{ 'campaign-id' : ?0 }", fields = "{ 'questionnaire-id' : 1, 'campaign-id' : 1 }")
    List<SurveyUnitDocument> findAllSummaryByCampaignId(String campaignId);

    /**
     * Find all survey unit summary by survey unit ids
     *
     * @param surveyUnitIds survey units we want to retrieve
     * @return List of {@link SurveyUnitDocument} survey unit summary
     */
    @Query(value = "{ '_id' : { '$in' : ?0 }}", fields = "{ 'questionnaire-id' : 1, 'campaign-id' : 1 }")
    List<SurveyUnitDocument> findAllSummaryByIdIn(List<String> surveyUnitIds);

    /**
     * Retrieve a survey unit with all details
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnitDocument} survey unit
     */
    @Query(value = "{ '_id' : ?0 }")
    Optional<SurveyUnitDocument> findOneById(String surveyUnitId);

    /**
     * Retrieve all survey units with all details
     *
     * @return List of {@link SurveyUnitDocument} survey units
     */
    @Query(value = "{ }", sort = "{ '_id' : 1 }")
    List<SurveyUnitDocument> findAllSurveyUnits();

    /**
     * Retrieve a survey unit with campaign and state data linked (used for deposit proof)
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnitDocument} survey unit
     */
    @Query(value = "{ '_id' : ?0 }", fields = "{ 'questionnaire-id' : 1, 'campaign-id' : 1, 'state-data' : 1 }")
    Optional<SurveyUnitDocument> findWithCampaignAndStateById(String surveyUnitId);

    /**
     * Find all survey unit ids
     *
     * @return List of survey unit ids
     */
    @Query(value = "{ '_id' : ?0 }", fields = "{ '_id' : 1 }", sort = "{ '_id' : 1 }")
    Optional<List<SurveyUnitDocument>> findAllIds();

    /**
     * Search survey units by ids
     * @param surveyUnitIds ids to search
     * @return List of {@link SurveyUnitDocument} survey units found
     */
    @Query(value = "{ '_id' : { '$in' : ?0 }}", sort = "{ '_id' : 1 }")
    List<SurveyUnitDocument> findSurveyUnitsByIdIn(List<String> surveyUnitIds);

    /**
     * Find survey units with state linked by ids
     *
     * @param surveyUnitIds survey unit ids
     * @return List of {@link SurveyUnitDocument} survey units
     */
    @Query(value = "{ '_id' : { '$in' : ?0 }, 'state-data' : { '$exists' : 1 } }",
            fields = "{ 'questionnaire-id' : 1, 'campaign-id' : 1, 'state-data' : 1 }")
    List<SurveyUnitDocument> findWithState(List<String> surveyUnitIds);

    /**
     * Delete survey units linked to a campaign
     *
     * @param campaignId campaign id
     */
    @Transactional
    void deleteByCampaignId(String campaignId);

    /**
     * Retrieve a comment
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnitDocument} survey unit
     */
    @Query(value = "{ '_id' : ?0 }", fields = " { 'comment' : 1 }")
    Optional<SurveyUnitDocument> findComment(String surveyUnitId);

    /**
     * Retrieve a data
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnitDocument} survey unit
     */
    @Query(value = "{ '_id' : ?0 }", fields = " { 'data' : 1 }")
    Optional<SurveyUnitDocument> findData(String surveyUnitId);

    /**
     * Retrieve a personalization
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnitDocument} survey unit
     */
    @Query(value = "{ '_id' : ?0 }", fields = " { 'personalization' : 1 }")
    Optional<SurveyUnitDocument> findPersonalization(String surveyUnitId);

    /**
     * Retrieve a comment
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnitDocument} survey unit
     */
    @Query(value = "{ '_id' : ?0 }", fields = " { 'state-data' : 1 }")
    Optional<SurveyUnitDocument> findStateData(String surveyUnitId);

    @Query(value = "{ '_id' : ?0 }")
    @Update("{ '$set': { 'personalization': ?1 } }")
    void savePersonalization(String surveyUnitId, PersonalizationObject personalization);

    @Query(value = "{ '_id' : ?0 }")
    @Update("{ '$set': { 'data': ?1 } }")
    void saveData(String surveyUnitId, DataObject data);

    @Query(value = "{ '_id' : ?0 }")
    @Update("{ '$set': { 'comment': ?1 } }")
    void saveComment(String surveyUnitId, CommentObject comment);

    @Query(value = "{ '_id' : ?0 }")
    @Update("{ '$set': { 'state-data': ?1 } }")
    void saveStateData(String surveyUnitId, StateDataObject data);

    @Query(value = "{ '_id' : ?0 }")
    @Update("{ '$set': { 'data.COLLECTED': ?1 } }")
    void updateCollectedData(String surveyUnitId, ObjectNode partialCollectedDataNode);

    /**
     *
     * @param surveyUnitId survey unit id
     * @return state data existence for a survey unit
     */
    @Query(value = "{ '_id' : ?0, 'state-data' : { '$exists' : 1 }}")
    boolean existsStateDataBySurveyUnitId(String surveyUnitId);
}
