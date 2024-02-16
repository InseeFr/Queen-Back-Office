package fr.insee.queen.domain.campaign.infrastructure.dummy;

import fr.insee.queen.domain.campaign.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class QuestionnaireModelFakeRepository implements QuestionnaireModelRepository {

    @Setter
    private boolean questionnaireExists = true;

    @Override
    public List<String> findAllIds(String campaignId) {
        return null;
    }

    @Override
    public Optional<String> findQuestionnaireData(String questionnaireId) {
        return Optional.empty();
    }

    @Override
    public boolean exists(String questionnaireId) {
        return questionnaireExists;
    }

    @Override
    public void create(QuestionnaireModel questionnaireData) {

    }

    @Override
    public void update(QuestionnaireModel questionnaireData) {

    }

    @Override
    public Long countValidQuestionnaires(String campaignId, Set<String> questionnaireIds) {
        return null;
    }

    @Override
    public void deleteAllFromCampaign(String campaignId) {

    }

    @Override
    public List<String> findAllQuestionnaireDatas(String campaignId) {
        return null;
    }
}
