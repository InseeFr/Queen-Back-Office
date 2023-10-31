package fr.insee.queen.api.service.campaign;

import fr.insee.queen.api.domain.CampaignData;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;

import java.util.List;

public interface CampaignService {
	List<CampaignSummaryDto> getAllCampaigns();
	void delete(String campaignId);
	void createCampaign(CampaignData campaignData);
	void updateCampaign(CampaignData campaignData);
}
