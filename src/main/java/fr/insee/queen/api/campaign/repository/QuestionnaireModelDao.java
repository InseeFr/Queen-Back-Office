package fr.insee.queen.api.campaign.repository;

import fr.insee.queen.api.campaign.repository.entity.CampaignDB;
import fr.insee.queen.api.campaign.repository.entity.NomenclatureDB;
import fr.insee.queen.api.campaign.repository.entity.QuestionnaireModelDB;
import fr.insee.queen.api.campaign.repository.jpa.CampaignJpaRepository;
import fr.insee.queen.api.campaign.repository.jpa.NomenclatureJpaRepository;
import fr.insee.queen.api.campaign.repository.jpa.QuestionnaireModelJpaRepository;
import fr.insee.queen.api.campaign.service.gateway.QuestionnaireModelRepository;
import fr.insee.queen.api.campaign.service.model.QuestionnaireModel;
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
    public Optional<String> findQuestionnaireData(String questionnaireId) {
        return jpaRepository.findQuestionnaireValue(questionnaireId);
    }

    @Override
    public boolean exists(String questionnaireId) {
        return jpaRepository.existsById(questionnaireId);
    }

    @Override
    @Transactional
    public void create(QuestionnaireModel questionnaireData) {
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.requiredNomenclatureIds());
        QuestionnaireModelDB questionnaire = new QuestionnaireModelDB(questionnaireData.id(), questionnaireData.label(), questionnaireData.value(), requiredNomenclatures);
        if (questionnaireData.campaignId() != null) {
            CampaignDB campaign = campaignJpaRepository.getReferenceById(questionnaireData.campaignId());
            questionnaire.campaign(campaign);
        }
        jpaRepository.save(questionnaire);
    }

    @Override
    public void update(QuestionnaireModel questionnaireData) {
        QuestionnaireModelDB questionnaire = jpaRepository.getReferenceById(questionnaireData.id());
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.requiredNomenclatureIds());
        questionnaire.label(questionnaireData.label());
        questionnaire.value(questionnaireData.value());
        questionnaire.nomenclatures(requiredNomenclatures);
        CampaignDB campaign = campaignJpaRepository.getReferenceById(questionnaireData.campaignId());
        questionnaire.campaign(campaign);

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
    public List<String> findAllQuestionnaireValues(String campaignId) {
        return jpaRepository.findAllValueByCampaignId(campaignId);
    }
}
