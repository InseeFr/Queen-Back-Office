package fr.insee.queen.api.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireModelData {
    private String id;
    private String label;
    private String value;
    private Set<String> requiredNomenclatureIds;
    private String campaignId;

    public static QuestionnaireModelData createQuestionnaireWithCampaign(String id, String label, String value, Set<String> requiredNomenclatureIds, @NonNull String campaignId) {
        return new QuestionnaireModelData(id, label, value, requiredNomenclatureIds, campaignId);
    }

    public static QuestionnaireModelData createQuestionnaireWithoutCampaign(String id, String label, String value, Set<String> requiredNomenclatureIds) {
        return new QuestionnaireModelData(id, label, value, requiredNomenclatureIds, null);
    }
}
