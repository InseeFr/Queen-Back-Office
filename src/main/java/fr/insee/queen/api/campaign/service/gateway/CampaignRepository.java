package fr.insee.queen.api.campaign.service.gateway;

import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.campaign.service.model.CampaignSummary;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository {

    void create(Campaign campaign);

    boolean exists(String campaignId);

    List<CampaignSummary> getAllWithQuestionnaireIds();

    void delete(String campaignId);

    Optional<CampaignSummary> findWithQuestionnaireIds(String campaignId);

    void update(Campaign campaign);

    Optional<String> findMetadataByCampaignId(String campaignId);

    Optional<String> findMetadataByQuestionnaireId(String questionnaireId);
}
