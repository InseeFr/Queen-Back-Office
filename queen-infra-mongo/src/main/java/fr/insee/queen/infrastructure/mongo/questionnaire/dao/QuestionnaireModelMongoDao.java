package fr.insee.queen.infrastructure.mongo.questionnaire.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.QuestionnaireModelDataObject;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.QuestionnaireModelDocument;
import fr.insee.queen.infrastructure.mongo.questionnaire.repository.QuestionnaireMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QuestionnaireModelMongoDao implements QuestionnaireModelRepository {
    private final QuestionnaireMongoRepository questionnaireRepository;

    @Override
    public List<String> findAllIds(String campaignId) {
        return questionnaireRepository.findQuestionnairesSummaryByCampaignId(campaignId)
                .stream()
                .map(QuestionnaireModelDocument::getId)
                .toList();
    }

    @Override
    public Optional<ObjectNode> findQuestionnaireData(String questionnaireId) {
        return questionnaireRepository.findQuestionnaireData(questionnaireId)
                .stream()
                .map(QuestionnaireModelDocument::getData)
                .map(QuestionnaireModelDataObject::toModel)
                .findFirst();
    }

    @Override
    public boolean exists(String questionnaireId) {
        return questionnaireRepository.existsById(questionnaireId);
    }

    @Override
    @Transactional
    public void create(QuestionnaireModel questionnaire) {
        QuestionnaireModelDocument questionnaireModelDocument = QuestionnaireModelDocument.fromModel(questionnaire);
        questionnaireRepository.save(questionnaireModelDocument);
    }

    @Override
    public void update(QuestionnaireModel questionnaire) {
        String questionnaireId = questionnaire.getId();

        QuestionnaireModelDocument questionnaireModelDocument = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Questionnaire %s not found", questionnaireId)));

        QuestionnaireModelDataObject data = QuestionnaireModelDataObject.fromModel(questionnaire.getValue());
        questionnaireModelDocument.setData(data);

        questionnaireRepository.updateQuestionnaire(questionnaireId, questionnaire.getLabel(), data, questionnaire.getRequiredNomenclatureIds());
    }

    @Override
    public Long countValidQuestionnaires(String campaignId, Set<String> questionnaireIds) {
        return questionnaireRepository.countValidQuestionnaires(campaignId, questionnaireIds);
    }

    @Override
    public void deleteAllFromCampaign(String campaignId) {
        questionnaireRepository.deleteByCampaignId(campaignId);
    }

    @Override
    public List<ObjectNode> findAllQuestionnaireDatas(String campaignId) {
        return questionnaireRepository.findAllQuestionnaireData(campaignId)
                .stream()
                .map(QuestionnaireModelDocument::getData)
                .map(QuestionnaireModelDataObject::toModel)
                .toList();
    }
}
