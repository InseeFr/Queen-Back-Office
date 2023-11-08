package fr.insee.queen.api.service.gateway;

import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelData;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelValueDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestionnaireModelRepository {
    List<String> findAllIds(String campaignId);
    Optional<QuestionnaireModelValueDto> findQuestionnaireValue(String questionnaireId);
    boolean exists(String questionnaireId);
    void create(QuestionnaireModelData questionnaireData);
    void update(QuestionnaireModelData questionnaireData);
    Long countValidQuestionnaires(String campaignId, Set<String> questionnaireIds);
    void deleteAllFromCampaign(String campaignId);
    List<String> findAllQuestionnaireValues(String campaignId);
}
