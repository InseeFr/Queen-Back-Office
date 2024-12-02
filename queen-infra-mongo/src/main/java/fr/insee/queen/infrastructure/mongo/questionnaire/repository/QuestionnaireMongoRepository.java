package fr.insee.queen.infrastructure.mongo.questionnaire.repository;

import fr.insee.queen.infrastructure.mongo.questionnaire.document.MetadataObject;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.QuestionnaireModelDataObject;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.QuestionnaireModelDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Mongo repository to handle campaigns
 */
@Repository
public interface QuestionnaireMongoRepository extends MongoRepository<QuestionnaireModelDocument, String> {

    /**
     * Retrieve the campaign.metadata json value of a campaign
     * @param campaignId campaign id
     * @return json campaign.metadata value
     */
    @Query(value = "{ 'campaign.id' : ?0 }", fields = "{ 'campaign.metadata' : 1 }")
    Optional<QuestionnaireModelDocument> findMetadataByCampaignId(String campaignId);

    /**
     * Retrieve the campaign.metadata json value of a campaign by the questionnaire id
     *
     * @param questionnaireId questionnaire id
     * @return json campaign.metadata value
     */

    @Query(value = "{ '_id' : ?0 }", fields = "{ 'campaign.metadata' : 1 }")
    Optional<QuestionnaireModelDocument> findMetadataByQuestionnaireId(String questionnaireId);

    /**
     * Find all summaries of campaigns
     *
     * @return all campaign summaries
     */
    @Query(value = "{ }", fields = "{ '_id' : 1, 'label' : 1, 'campaign._id' : 1, 'campaign.label' : 1, 'nomenclatures._id' : 1 }")
    List<QuestionnaireModelDocument> findAllQuestionnairesSummary();

    /**
     * Retrieve a questionnaire summary
     *
     * @param questionnaireId questionnaire id
     * @return a questionnaire summary
     */
    @Query(value = "{ 'id' : ?0 }", fields = "{ '_id' : 1, 'label' : 1, 'campaign._id' : 1, 'campaign.label' : 1, 'nomenclatures._id' : 1 }")
    Optional<QuestionnaireModelDocument> findQuestionnaireSummary(String questionnaireId);

    /**
     * Retrieve a campaign summary
     *
     * @param campaignId campaign id
     * @return a campaign summary
     */
    @Query(value = "{ 'campaign._id' : ?0 }", fields = "{ '_id' : 1, 'label' : 1, 'campaign._id' : 1, 'campaign.label' : 1, 'nomenclatures._id' : 1 }")
    List<QuestionnaireModelDocument> findQuestionnairesSummaryByCampaignId(String campaignId);

    /**
     * Find data structure for all questionnaire linked to a campaign
     *
     * @param campaignId campaign id
     * @return all questionnaire values for a campaign
     */
    @Query(value = "{ 'campaign.id' : ?0 }", fields = "{ 'data' : 1 }")
    List<QuestionnaireModelDocument> findAllQuestionnaireData(String campaignId);

    /**
     * Find data structure for a questionnaire
     *
     * @param questionnaireId questionnaire id
     * @return questionnaire data for a campaign
     */
    @Query(value = "{ '_id' : ?0 }", fields = "{ 'data' : 1 }")
    Optional<QuestionnaireModelDocument> findQuestionnaireData(String questionnaireId);

    /**
     *
     * @param questionnaireId questionnaire id
     * @return questionnaire existence
     */
    boolean existsById(String questionnaireId);

    @Query(value = "{ 'campaign.id' : ?0 }", fields = "{ 'nomenclatures._id' : 1 }")
    List<QuestionnaireModelDocument> findRequiredNomenclatureByCampaignId(String campaignId);

    @Query(value = "{ '_id' : ?0 }", fields = "{ 'nomenclatures._id' : 1 }")
    Optional<QuestionnaireModelDocument> findRequiredNomenclatureByQuestionnaireId(String questionnaireId);

    @Query(value = "{ 'campaign.id' : ?0 }", exists = true)
    boolean existsCampaignById(String campaignId);

    @Query(value = "{ '_id' : ?0 }")
    @Update(value = "{ '$set' : { 'campaign' : { '_id' : ?1, 'label' : ?2, 'metadata' : ?3 } } }")
    void updateCampaign(String questionnaireId, String campaignId, String campaignLabel, MetadataObject metadata);

    @Query(value = "{ '_id' : ?0 }")
    @Update(value = "{ '$set' : { 'label' : ?1, 'data' : ?2, 'nomenclatures' : ?3 } }")
    void updateQuestionnaire(String questionnaireId, String label, QuestionnaireModelDataObject data, Set<String> requiredNomenclatureIds);

    void deleteByCampaignId(String campaignId);

    @Query(value = """
            {
                '_id' : { '$in' : ?1 },
                '$or' : [
                    { 'campaign.id' : ?0 },
                    { 'campaign' : { '$exists' : 0 } }
                ]
            }""", count = true)
    Long countValidQuestionnaires(String campaignId, Set<String> questionnaireIds);

    @Query(value = "{ '_id' : ?0 }")
    @Update(value = "{ '$unset' : { 'campaign' : '' } }")
    void deleteCampaignFromQuestionnaire(String questionnaireId);
}
