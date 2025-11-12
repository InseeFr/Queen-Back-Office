package fr.insee.queen.domain.campaign.service.dummy;

import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.service.CampaignService;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
public class CampaignFakeService implements CampaignService {

    private boolean deleted = false;
    private boolean updated = false;
    private boolean created = false;
    public static final String CAMPAIGN1_ID = "allCampaigns1";
    public static final List<CampaignSummary> CAMPAIGN_SUMMARY_LIST = List.of(
            new CampaignSummary(CAMPAIGN1_ID, "label", CampaignSensitivity.NORMAL, Set.of("questionnaireId1", "questionnaireId2")),
            new CampaignSummary("allCampaigns2", "label", CampaignSensitivity.SENSITIVE, Set.of("questionnaireId1", "questionnaireId2"))
    );

    @Override
    public List<CampaignSummary> getAllCampaigns() {
        return CAMPAIGN_SUMMARY_LIST;
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

    @Override
    public Campaign getCampaign(String campaignId) {
        return null;
    }

    @Override
    public List<String> getAllCampaignIds() {
        return List.of(CAMPAIGN1_ID);
    }
}
