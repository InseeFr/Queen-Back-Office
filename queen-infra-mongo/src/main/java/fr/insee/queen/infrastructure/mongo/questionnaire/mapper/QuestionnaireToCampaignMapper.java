package fr.insee.queen.infrastructure.mongo.questionnaire.mapper;

import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.CampaignObject;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.QuestionnaireModelDocument;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionnaireToCampaignMapper {
    private QuestionnaireToCampaignMapper() {
        throw new IllegalArgumentException("Utility class");
    }

    public static List<CampaignSummary> toCampaignsSummary(List<QuestionnaireModelDocument> questionnaires) {
        return questionnaires.stream()
                .map(QuestionnaireModelDocument::getCampaign)
                .filter(Objects::nonNull)
                .map(CampaignObject::getId)
                .distinct()
                .map(campaignId -> {
                    Set<String> questionnairesId = questionnaires.stream()
                            .map(QuestionnaireModelDocument::getId)
                            .filter(campaignId::equals)
                            .collect(Collectors.toSet());
                    return new CampaignSummary(campaignId, null, questionnairesId);
                })
                .toList();
    }

    public static CampaignSummary toCampaignSummary(@NonNull String campaignId, List<QuestionnaireModelDocument> questionnaires) {
        Set<String> questionnairesId = questionnaires.stream()
                .map(QuestionnaireModelDocument::getId)
                .filter(campaignId::equals)
                .collect(Collectors.toSet());
        return new CampaignSummary(campaignId, null, questionnairesId);
    }
}
