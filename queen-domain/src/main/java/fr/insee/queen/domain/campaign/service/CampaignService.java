package fr.insee.queen.domain.campaign.service;

import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSummary;

import java.util.List;

public interface CampaignService {
    List<CampaignSummary> getAllCampaigns();

    void delete(String campaignId);

    void createCampaign(Campaign campaignData);

    void updateCampaign(Campaign campaignData);

    Campaign getCampaign(String campaignId);
}
