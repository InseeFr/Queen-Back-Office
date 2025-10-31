package fr.insee.queen.domain.campaign.service;

import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSummary;

import java.util.List;
import java.util.Optional;

public interface CampaignService {
    List<CampaignSummary> getAllCampaigns();

    void delete(String campaignId);

    void createCampaign(Campaign campaignData);

    Optional<String> findCampaignIdFromQuestionnaireId(String questionnaireId);

    void updateCampaign(Campaign campaignData);

    Campaign getCampaign(String campaignId);

    List<String> getAllCampaignIds();
}
