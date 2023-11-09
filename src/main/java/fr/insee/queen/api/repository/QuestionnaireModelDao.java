package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelData;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelCampaignDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelValueDto;
import fr.insee.queen.api.repository.entity.CampaignDB;
import fr.insee.queen.api.repository.entity.NomenclatureDB;
import fr.insee.queen.api.repository.entity.QuestionnaireModelDB;
import fr.insee.queen.api.repository.jpa.CampaignJpaRepository;
import fr.insee.queen.api.repository.jpa.NomenclatureJpaRepository;
import fr.insee.queen.api.repository.jpa.QuestionnaireModelJpaRepository;
import fr.insee.queen.api.service.gateway.QuestionnaireModelRepository;
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

    public List<String> findAllIds(String campaignId) {
        return jpaRepository.findAllIdByCampaignId(campaignId);
    }

    public Optional<QuestionnaireModelValueDto> findQuestionnaireValue(String questionnaireId) {
        return jpaRepository.findQuestionnaireModelById(questionnaireId);
    }

    public List<QuestionnaireModelDB> findByCampaignId(String questionnaireId) {
        return jpaRepository.findByCampaignId(questionnaireId);
    }

    public Optional<QuestionnaireModelCampaignDto> findQuestionnaireModelWithCampaignById(String questionnaireId) {
        return jpaRepository.findQuestionnaireModelWithCampaignById(questionnaireId);
    }

    public boolean exists(String questionnaireId) {
        return jpaRepository.existsById(questionnaireId);
    }

    @Transactional
    public void create(QuestionnaireModelData questionnaireData) {
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.requiredNomenclatureIds());
        QuestionnaireModelDB questionnaire = new QuestionnaireModelDB(questionnaireData.id(), questionnaireData.label(), questionnaireData.value(), requiredNomenclatures);
        if(questionnaireData.campaignId() != null) {
            CampaignDB campaign = campaignJpaRepository.getReferenceById(questionnaireData.campaignId());
            questionnaire.campaign(campaign);
        }
        jpaRepository.save(questionnaire);
    }

    public void update(QuestionnaireModelData questionnaireData) {
        QuestionnaireModelDB questionnaire = jpaRepository.getReferenceById(questionnaireData.id());
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.requiredNomenclatureIds());
        questionnaire.label(questionnaireData.label());
        questionnaire.value(questionnaireData.value());
        questionnaire.nomenclatures(requiredNomenclatures);
        CampaignDB campaign = campaignJpaRepository.getReferenceById(questionnaireData.campaignId());
        questionnaire.campaign(campaign);

        jpaRepository.save(questionnaire);
    }

    public Long countValidQuestionnaires(String campaignId, Set<String> questionnaireIds) {
        return jpaRepository.countValidQuestionnairesByIds(campaignId, questionnaireIds);
    }

    public void deleteAllFromCampaign(String campaignId) {
        jpaRepository.deleteAllByCampaignId(campaignId);
    }

    public List<String> findAllQuestionnaireValues(String campaignId) {
        return jpaRepository.findAllValueByCampaignId(campaignId);
    }
}
