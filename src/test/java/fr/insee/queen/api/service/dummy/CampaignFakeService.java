package fr.insee.queen.api.service.dummy;

import fr.insee.queen.api.campaign.service.CampaignService;
import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.campaign.service.model.CampaignSummary;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class CampaignFakeService implements CampaignService {

    private boolean deleted = false;
    private boolean updated = false;
    private boolean created = false;
    private boolean allCampaignsRetrieved = false;
    public static final String CAMPAIGN1_ID = "allCampaigns1";

    @Override
    public List<CampaignSummary> getAllCampaigns() {
        allCampaignsRetrieved = true;
        return List.of(
                new CampaignSummary(CAMPAIGN1_ID, "label", Set.of("questionnaireId1", "questionnaireId2")),
                new CampaignSummary("allCampaigns2", "label", Set.of("questionnaireId1", "questionnaireId2"))
        );
    }

    @Override
    public void delete(String campaignId) {
        this.deleted = true;
    }

    @Override
    public void createCampaign(Campaign campaignData) {
        created = true;
    }

    @Override
    public void updateCampaign(Campaign campaignData) {
        updated = true;
    }
}
