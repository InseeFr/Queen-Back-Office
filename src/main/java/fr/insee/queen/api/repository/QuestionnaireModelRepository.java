package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelCampaignDto;
import fr.insee.queen.api.domain.QuestionnaireModelData;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelValueDto;
import fr.insee.queen.api.entity.CampaignDB;
import fr.insee.queen.api.entity.NomenclatureDB;
import fr.insee.queen.api.entity.QuestionnaireModelDB;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@AllArgsConstructor
public class QuestionnaireModelRepository {
    private final QuestionnaireModelCrudRepository crudRepository;

    private final CampaignCrudRepository campaignCrudRepository;

    private final NomenclatureRepository nomenclatureRepository;

    public List<String> findAllIdByCampaignId(String campaignId) {
        return crudRepository.findAllIdByCampaignId(campaignId);
    }

    public Optional<QuestionnaireModelValueDto> findQuestionnaireModelById(String questionnaireId) {
        return crudRepository.findQuestionnaireModelById(questionnaireId);
    }

    public List<QuestionnaireModelDB> findByCampaignId(String questionnaireId) {
        return crudRepository.findByCampaignId(questionnaireId);
    }

    public Optional<QuestionnaireModelCampaignDto> findQuestionnaireModelWithCampaignById(String questionnaireId) {
        return crudRepository.findQuestionnaireModelWithCampaignById(questionnaireId);
    }

    public boolean existsById(String questionnaireId) {
        return crudRepository.existsById(questionnaireId);
    }

    @Transactional
    public void createQuestionnaire(QuestionnaireModelData questionnaireData) {
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.requiredNomenclatureIds());
        QuestionnaireModelDB questionnaire = new QuestionnaireModelDB(questionnaireData.id(), questionnaireData.label(), questionnaireData.value(), requiredNomenclatures);
        if(questionnaireData.campaignId() != null) {
            CampaignDB campaign = campaignCrudRepository.getReferenceById(questionnaireData.campaignId());
            questionnaire.campaign(campaign);
        }
        crudRepository.save(questionnaire);
    }

    public void updateQuestionnaire(QuestionnaireModelData questionnaireData) {
        QuestionnaireModelDB questionnaire = crudRepository.getReferenceById(questionnaireData.id());
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(questionnaireData.requiredNomenclatureIds());
        questionnaire.label(questionnaireData.label());
        questionnaire.value(questionnaireData.value());
        questionnaire.nomenclatures(requiredNomenclatures);
        CampaignDB campaign = campaignCrudRepository.getReferenceById(questionnaireData.campaignId());
        questionnaire.campaign(campaign);

        crudRepository.save(questionnaire);
    }

    public Long countValidQuestionnaires(String campaignId, Set<String> questionnaireIds) {
        return crudRepository.countValidQuestionnairesByIds(campaignId, questionnaireIds);
    }

    public void deleteAllFromCampaign(String campaignId) {
        crudRepository.deleteAllByCampaignId(campaignId);
    }

    public List<String> findAllValueByCampaignId(String campaignId) {
        return crudRepository.findAllValueByCampaignId(campaignId);
    }
}
