package fr.insee.queen.api.campaign.service;

import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.campaign.service.model.CampaignSummary;

import java.util.List;

public interface CampaignService {
    List<CampaignSummary> getAllCampaigns();

    void delete(String campaignId);

    void createCampaign(Campaign campaignData);

    void updateCampaign(Campaign campaignData);
}
