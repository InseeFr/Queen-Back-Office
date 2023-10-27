package fr.insee.queen.api.service.campaign;

import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.input.CampaignInputDto;

import java.util.List;

public interface CampaignService {
	List<CampaignSummaryDto> getAllCampaigns();
	void delete(String campaignId);
	void createCampaign(CampaignInputDto campaign);
	void updateCampaign(CampaignInputDto campaign);
}
