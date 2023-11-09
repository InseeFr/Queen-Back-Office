package fr.insee.queen.api.service.dummy;

import fr.insee.queen.api.domain.CampaignData;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.service.campaign.CampaignService;
import lombok.Getter;

import java.util.List;

@Getter
public class CampaignFakeService implements CampaignService {

    private boolean deleted = false;
    private boolean updated = false;
    private boolean created = false;
    private boolean allCampaignsRetrieved = false;
    public static final String CAMPAIGN1_ID = "allCampaigns1";

    @Override
    public List<CampaignSummaryDto> getAllCampaigns() {
        allCampaignsRetrieved = true;
        return List.of(
                new CampaignSummaryDto(CAMPAIGN1_ID, List.of("questionnaireId1", "questionnaireId2")),
                new CampaignSummaryDto("allCampaigns2", List.of("questionnaireId1", "questionnaireId2"))
        );
    }

    @Override
    public void delete(String campaignId) {
        this.deleted = true;
    }

    @Override
    public void createCampaign(CampaignData campaignData) {
        created = true;
    }

    @Override
    public void updateCampaign(CampaignData campaignData) {
        updated = true;
    }
}
