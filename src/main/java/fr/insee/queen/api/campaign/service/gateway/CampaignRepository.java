package fr.insee.queen.api.campaign.service.gateway;

import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.campaign.service.model.CampaignSummary;

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
    Optional<String> findMetadataByCampaignId(String campaignId);

    /**
     * Retrieve the metadata json value of a campaign byt the questionnaire id
     *
     * @param questionnaireId questionnaire id
     * @return {@link String} json metadata value
     */
    Optional<String> findMetadataByQuestionnaireId(String questionnaireId);
}
