package fr.insee.queen.infrastructure.db.campaign.repository;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import fr.insee.queen.infrastructure.db.campaign.entity.CampaignDB;
import fr.insee.queen.infrastructure.db.campaign.entity.NomenclatureDB;
import fr.insee.queen.infrastructure.db.campaign.entity.QuestionnaireModelDB;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.CampaignJpaRepository;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.NomenclatureJpaRepository;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.QuestionnaireModelJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@AllArgsConstructor
public class QuestionnaireModelDao implements QuestionnaireModelRepository {
    private final QuestionnaireModelJpaRepository jpaRepository;
    private final CampaignJpaRepository campaignJpaRepository;
    private final NomenclatureJpaRepository nomenclatureRepository;

    @Override
    public List<String> findAllIds(String campaignId) {
        return jpaRepository.findAllIdByCampaignId(campaignId);
    }

    @Override
    public Optional<ObjectNode> findQuestionnaireData(String questionnaireId) {
        return jpaRepository.findQuestionnaireData(questionnaireId);
    }

    @Override
    public boolean exists(String questionnaireId) {
        return jpaRepository.existsById(questionnaireId);
    }

    @Override
    @Transactional
    public void create(QuestionnaireModel questionnaireData) {
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.getRequiredNomenclatureIds());
        QuestionnaireModelDB questionnaire = new QuestionnaireModelDB(questionnaireData.getId(), questionnaireData.getLabel(), questionnaireData.getValue(), requiredNomenclatures);
        if (questionnaireData.getCampaignId() != null) {
            CampaignDB campaign = campaignJpaRepository.getReferenceById(questionnaireData.getCampaignId());
            questionnaire.setCampaign(campaign);
        }
        jpaRepository.save(questionnaire);
    }

    @Override
    public void update(QuestionnaireModel questionnaireData) {
        QuestionnaireModelDB questionnaire = jpaRepository.getReferenceById(questionnaireData.getId());
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.getRequiredNomenclatureIds());
        questionnaire.setLabel(questionnaireData.getLabel());
        questionnaire.setValue(questionnaireData.getValue());
        questionnaire.setNomenclatures(requiredNomenclatures);
        CampaignDB campaign = campaignJpaRepository.getReferenceById(questionnaireData.getCampaignId());
        questionnaire.setCampaign(campaign);

        jpaRepository.save(questionnaire);
    }

    @Override
    public Long countValidQuestionnaires(String campaignId, Set<String> questionnaireIds) {
        return jpaRepository.countValidQuestionnairesByIds(campaignId, questionnaireIds);
    }

    @Override
    public void deleteAllFromCampaign(String campaignId) {
        jpaRepository.deleteAllByCampaignId(campaignId);
    }

    @Override
    public List<ObjectNode> findAllQuestionnaireDatas(String campaignId) {
        return jpaRepository.findAllValueByCampaignId(campaignId);
    }
}
