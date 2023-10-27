package fr.insee.queen.api.service.campaign;

public interface CampaignExistenceService {
    void throwExceptionIfCampaignNotExist(String campaignId);
    void throwExceptionIfCampaignAlreadyExist(String campaignId);
    boolean existsById(String campaignId);
}
