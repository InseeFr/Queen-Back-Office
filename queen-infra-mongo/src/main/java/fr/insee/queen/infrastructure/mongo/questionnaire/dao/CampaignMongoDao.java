package fr.insee.queen.infrastructure.mongo.questionnaire.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.gateway.CampaignRepository;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.CampaignObject;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.MetadataObject;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.QuestionnaireModelDocument;
import fr.insee.queen.infrastructure.mongo.questionnaire.mapper.QuestionnaireToCampaignMapper;
import fr.insee.queen.infrastructure.mongo.questionnaire.repository.QuestionnaireMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CampaignMongoDao implements CampaignRepository {

    private final QuestionnaireMongoRepository questionnaireRepository;

    @Override
    @Transactional
    public void create(Campaign campaign) {
        campaign.getQuestionnaireIds().forEach(questionnaireId -> {
            MetadataObject metadata = MetadataObject.fromModel(campaign.getMetadata());
            questionnaireRepository.updateCampaign(questionnaireId, campaign.getId(), campaign.getLabel(), metadata);
        });
    }

    @Override
    public boolean exists(String campaignId) {
        return questionnaireRepository.existsCampaignById(campaignId);
    }

    @Override
    public List<CampaignSummary> getAllWithQuestionnaireIds() {
        List<QuestionnaireModelDocument> questionnaires = questionnaireRepository.findAllQuestionnairesSummary();
        return QuestionnaireToCampaignMapper.toCampaignsSummary(questionnaires);
    }

    @Override
    public void delete(String campaignId) {
        questionnaireRepository.deleteByCampaignId(campaignId);
    }

    @Override
    public Optional<CampaignSummary> findWithQuestionnaireIds(String campaignId) {
        List<QuestionnaireModelDocument> questionnaires = questionnaireRepository.findQuestionnairesSummaryByCampaignId(campaignId);
        if(questionnaires.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(QuestionnaireToCampaignMapper.toCampaignSummary(campaignId, questionnaires));
    }

    @Override
    @Transactional
    public void update(Campaign campaign) {
        MetadataObject metadata = MetadataObject.fromModel(campaign.getMetadata());

        questionnaireRepository.findQuestionnairesSummaryByCampaignId(campaign.getId()).stream()
                .map(QuestionnaireModelDocument::getId)
                .filter(questionnaireId -> !questionnaireId.contains(campaign.getId()))
                .forEach(questionnaireRepository::deleteCampaignFromQuestionnaire);

        campaign.getQuestionnaireIds().forEach(questionnaireId ->
            questionnaireRepository.updateCampaign(questionnaireId, campaign.getId(), campaign.getLabel(), metadata)
        );
    }

    @Override
    public Optional<ObjectNode> findMetadataByCampaignId(String campaignId) {
        return questionnaireRepository.findMetadataByCampaignId(campaignId).stream()
                .findFirst()
                .map(QuestionnaireModelDocument::getCampaign)
                .map(CampaignObject::getMetadata)
                .map(MetadataObject::toModel);
    }

    @Override
    public Optional<ObjectNode> findMetadataByQuestionnaireId(String questionnaireId) {
        return questionnaireRepository.findMetadataByQuestionnaireId(questionnaireId)
                .map(QuestionnaireModelDocument::getCampaign)
                .map(CampaignObject::getMetadata)
                .map(MetadataObject::toModel);
    }
}
