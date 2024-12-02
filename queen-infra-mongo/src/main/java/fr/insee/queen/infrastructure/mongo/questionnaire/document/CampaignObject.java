package fr.insee.queen.infrastructure.mongo.questionnaire.document;

import fr.insee.queen.domain.campaign.model.CampaignSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import java.util.HashSet;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CampaignObject {

    @Id
    private String id;

    @Field("label")
    private String label;

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_EMPTY)
    private MetadataObject metadata;

    private CampaignObject(String id) {
        this.id = id;
    }

    public static CampaignObject fromModel(String campaignId) {
        return new CampaignObject(campaignId);
    }

    public static CampaignSummary toModel(CampaignObject campaignObject) {
        return new CampaignSummary(campaignObject.getId(), campaignObject.getLabel(), new HashSet<>());
    }
}
