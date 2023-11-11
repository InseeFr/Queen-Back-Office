package fr.insee.queen.api.service.gateway;

import fr.insee.queen.api.dto.campaign.CampaignData;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.metadata.MetadataDto;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository {

    void create(CampaignData campaignData);

    Boolean exists(String campaignId);

    List<CampaignSummaryDto> getAllWithQuestionnaireIds();

    void delete(String campaignId);

    Optional<CampaignSummaryDto> findWithQuestionnaireIds(String campaignId);

    void update(CampaignData campaignData);

    Optional<MetadataDto> findMetadataByCampaignId(String campaignId);

    Optional<MetadataDto> findMetadataByQuestionnaireId(String questionnaireId);
}
