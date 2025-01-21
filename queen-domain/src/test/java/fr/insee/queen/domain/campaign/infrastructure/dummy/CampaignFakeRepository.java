package fr.insee.queen.domain.campaign.infrastructure.dummy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.gateway.CampaignRepository;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CampaignFakeRepository implements CampaignRepository {

    @Setter
    private boolean campaignExists = true;

    @Setter
    private ObjectNode metadata;

    public static final String QUESTIONNAIRE_LINKED_ID = "id-questionnaire1";

    public static final String CAMPAIGN_ID = "id-campaign";

    @Override
    public void create(Campaign campaign) {
        // not used at this moment
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
        // not used at this moment
    }

    @Override
    public Optional<CampaignSummary> findWithQuestionnaireIds(String campaignId) {
        if(campaignExists) {
            return Optional.of(new CampaignSummary(CAMPAIGN_ID,
                    "label",
                    CampaignSensitivity.NORMAL,
                    Set.of(QUESTIONNAIRE_LINKED_ID)));
        }
        return Optional.empty();
    }

    @Override
    public void update(Campaign campaign) {
        // not used at this moment
    }

    @Override
    public Optional<ObjectNode> findMetadataByCampaignId(String campaignId) {
        if(metadata != null) {
            return Optional.of(metadata);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ObjectNode> findMetadataByQuestionnaireId(String questionnaireId) {
        if(metadata != null) {
            return Optional.of(metadata);
        }
        return Optional.empty();
    }
}
