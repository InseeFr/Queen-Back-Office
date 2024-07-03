package fr.insee.queen.domain.campaign.gateway;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSummary;

import java.util.List;
import java.util.Optional;

/**
 * Repository to handle campaigns
 */
public interface CampaignRepository {

    /**
     * Create campaign
     * @param campaign campaign to create
     */
    void create(Campaign campaign);

    /**
     * Check if campaign exists
     *
     * @param campaignId campaign id to check
     * @return true if exists, false otherwise
     */
    boolean exists(String campaignId);

    /**
     * Retrieve all campaigns summary
     *
     * @return List of {@link CampaignSummary} campaigns
     */
    List<CampaignSummary> getAllWithQuestionnaireIds();

    /**
     * Delete campaign
     *
     * @param campaignId campaign id
     */
    void delete(String campaignId);

    /**
     * Retrieve campaign summary
     *
     * @param campaignId campaign id
     * @return {@link CampaignSummary} campaign
     */
    Optional<CampaignSummary> findWithQuestionnaireIds(String campaignId);

    /**
     * Update campaign
     *
     * @param campaign campaign to update
     */
    void update(Campaign campaign);

    /**
     * Retrieve the metadata json value of a campaign
     *
     * @param campaignId campaign id
     * @return {@link String} json metadata value
     */
    Optional<ObjectNode> findMetadataByCampaignId(String campaignId);

    /**
     * Retrieve the metadata json value of a campaign byt the questionnaire id
     *
     * @param questionnaireId questionnaire id
     * @return {@link String} json metadata value
     */
    Optional<ObjectNode> findMetadataByQuestionnaireId(String questionnaireId);

    /**
     * Find campaign
     *
     * @param campaignId campaign id
     */
    Optional<Campaign> findCampaign(String campaignId);

    /**
     * Retrieve all campaigns ids
     *
     * @return List of {@link String} campaigns ids
     */
    List<String> getAllCampaignIds();


    /**
     * return the campaign from the questionnaire id
     * @param questionnaireId questionnaire id
     * @return the campaign id if found
     */
    Optional<String> findCampaignIdByQuestionnaireId(String questionnaireId);
}
