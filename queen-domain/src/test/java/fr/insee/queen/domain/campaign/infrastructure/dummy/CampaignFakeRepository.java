package fr.insee.queen.domain.campaign.infrastructure.dummy;

import fr.insee.queen.domain.campaign.gateway.CampaignRepository;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CampaignFakeRepository implements CampaignRepository {

    @Setter
    private boolean campaignExists = true;

    public final static String QUESTIONNAIRE_LINKED_ID = "id-questionnaire1";

    public final static String CAMPAIGN_ID = "id-campaign";

    @Override
    public void create(Campaign campaign) {

    }

    @Override
    public boolean exists(String campaignId) {
        return campaignExists;
    }

    @Override
    public List<CampaignSummary> getAllWithQuestionnaireIds() {
        return null;
    }

    @Override
    public void delete(String campaignId) {

    }

    @Override
    public Optional<CampaignSummary> findWithQuestionnaireIds(String campaignId) {
        if(campaignExists) {
            return Optional.of(new CampaignSummary(CAMPAIGN_ID,
                    "label",
                    Set.of(QUESTIONNAIRE_LINKED_ID)));
        }
        return Optional.empty();
    }

    @Override
    public void update(Campaign campaign) {

    }

    @Override
    public Optional<String> findMetadataByCampaignId(String campaignId) {
        return Optional.empty();
    }

    @Override
    public Optional<String> findMetadataByQuestionnaireId(String questionnaireId) {
        return Optional.empty();
    }
}
