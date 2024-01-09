package fr.insee.queen.domain.campaign.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireModel {
    private String id;
    private String campaignId;
    private String label;
    private String value;
    private Set<String> requiredNomenclatureIds;

    public static QuestionnaireModel createQuestionnaireWithCampaign(String id, String label, String value, Set<String> requiredNomenclatureIds, @NonNull String campaignId) {
        return new QuestionnaireModel(id, campaignId, label, value, requiredNomenclatureIds);
    }

    public static QuestionnaireModel createQuestionnaireWithoutCampaign(String id, String label, String value, Set<String> requiredNomenclatureIds) {
        return new QuestionnaireModel(id, null, label, value, requiredNomenclatureIds);
    }
}
