package fr.insee.queen.domain.campaign.service;

public interface CampaignExistenceService {
    void throwExceptionIfCampaignNotExist(String campaignId);

    void throwExceptionIfCampaignAlreadyExist(String campaignId);

    void throwExceptionIfCampaignNotLinkedToQuestionnaire(String campaignId, String questionnaireId);

    boolean existsById(String campaignId);
}
