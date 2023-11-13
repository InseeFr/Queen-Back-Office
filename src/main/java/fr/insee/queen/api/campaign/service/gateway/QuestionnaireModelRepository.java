package fr.insee.queen.api.campaign.service.gateway;

import fr.insee.queen.api.campaign.service.model.QuestionnaireModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestionnaireModelRepository {
    List<String> findAllIds(String campaignId);

    Optional<String> findQuestionnaireData(String questionnaireId);

    boolean exists(String questionnaireId);

    void create(QuestionnaireModel questionnaireData);

    void update(QuestionnaireModel questionnaireData);

    Long countValidQuestionnaires(String campaignId, Set<String> questionnaireIds);

    void deleteAllFromCampaign(String campaignId);

    List<String> findAllQuestionnaireValues(String campaignId);
}
