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
    private final QuestionnaireModelJpaRepository crudRepository;

    private final CampaignJpaRepository campaignJpaRepository;

    private final NomenclatureJpaRepository nomenclatureRepository;

    public List<String> findAllIds(String campaignId) {
        return crudRepository.findAllIdByCampaignId(campaignId);
    }

    public Optional<QuestionnaireModelValueDto> findQuestionnaireValue(String questionnaireId) {
        return crudRepository.findQuestionnaireModelById(questionnaireId);
    }

    public List<QuestionnaireModelDB> findByCampaignId(String questionnaireId) {
        return crudRepository.findByCampaignId(questionnaireId);
    }

    public Optional<QuestionnaireModelCampaignDto> findQuestionnaireModelWithCampaignById(String questionnaireId) {
        return crudRepository.findQuestionnaireModelWithCampaignById(questionnaireId);
    }

    public boolean exists(String questionnaireId) {
        return crudRepository.existsById(questionnaireId);
    }

    @Transactional
    public void create(QuestionnaireModelData questionnaireData) {
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.requiredNomenclatureIds());
        QuestionnaireModelDB questionnaire = new QuestionnaireModelDB(questionnaireData.id(), questionnaireData.label(), questionnaireData.value(), requiredNomenclatures);
        if(questionnaireData.campaignId() != null) {
            CampaignDB campaign = campaignJpaRepository.getReferenceById(questionnaireData.campaignId());
            questionnaire.campaign(campaign);
        }
        crudRepository.save(questionnaire);
    }

    public void update(QuestionnaireModelData questionnaireData) {
        QuestionnaireModelDB questionnaire = crudRepository.getReferenceById(questionnaireData.id());
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.requiredNomenclatureIds());
        questionnaire.label(questionnaireData.label());
        questionnaire.value(questionnaireData.value());
        questionnaire.nomenclatures(requiredNomenclatures);
        CampaignDB campaign = campaignJpaRepository.getReferenceById(questionnaireData.campaignId());
        questionnaire.campaign(campaign);

        crudRepository.save(questionnaire);
    }

    public Long countValidQuestionnaires(String campaignId, Set<String> questionnaireIds) {
        return crudRepository.countValidQuestionnairesByIds(campaignId, questionnaireIds);
    }

    public void deleteAllFromCampaign(String campaignId) {
        crudRepository.deleteAllByCampaignId(campaignId);
    }

    public List<String> findAllQuestionnaireValues(String campaignId) {
        return crudRepository.findAllValueByCampaignId(campaignId);
    }
}
