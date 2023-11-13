package fr.insee.queen.api.campaign.service;

public interface CampaignExistenceService {
    void throwExceptionIfCampaignNotExist(String campaignId);

    void throwExceptionIfCampaignAlreadyExist(String campaignId);

    boolean existsById(String campaignId);
}
