package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelCampaignDto;
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
    public void createQuestionnaire(String questionnaireId, String label, String value, Set<String> nomenclatureIds, String campaignId) {
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(nomenclatureIds);
        QuestionnaireModelDB questionnaire = new QuestionnaireModelDB(questionnaireId, label, value, requiredNomenclatures);
        if(campaignId != null) {
            CampaignDB campaign = campaignCrudRepository.getReferenceById(campaignId);
            questionnaire.campaign(campaign);
        }
        crudRepository.save(questionnaire);
    }

    public void updateQuestionnaire(String questionnaireId, String label, String value, Set<String> nomenclatureIds, String campaignId) {
        QuestionnaireModelDB questionnaire = crudRepository.getReferenceById(questionnaireId);
        Set<NomenclatureDB> requiredNomenclatures = nomenclatureRepository.findAllByIdIn(nomenclatureIds);
        questionnaire.label(label);
        questionnaire.value(value);
        questionnaire.nomenclatures(requiredNomenclatures);
        CampaignDB campaign = campaignCrudRepository.getReferenceById(campaignId);
        questionnaire.campaign(campaign);

        crudRepository.save(questionnaire);
    }

    public Long countValidQuestionnaires(String campaignId, List<String> questionnaireIds) {
        return crudRepository.countValidQuestionnairesByIds(campaignId, questionnaireIds);
    }

    public void deleteAllFromCampaign(String campaignId) {
        crudRepository.deleteAllByCampaignId(campaignId);
    }

    public List<String> findAllValueByCampaignId(String campaignId) {
        return crudRepository.findAllValueByCampaignId(campaignId);
    }
}
