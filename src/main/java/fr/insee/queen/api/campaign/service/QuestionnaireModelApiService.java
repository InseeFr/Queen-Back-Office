package fr.insee.queen.api.campaign.service;

import fr.insee.queen.api.campaign.service.exception.QuestionnaireModelServiceException;
import fr.insee.queen.api.campaign.service.gateway.QuestionnaireModelRepository;
import fr.insee.queen.api.campaign.service.model.QuestionnaireModel;
import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionnaireModelApiService implements QuestionnaireModelService {

    private final CampaignExistenceService campaignExistenceService;
    private final QuestionnaireModelExistenceService questionnaireModelExistenceService;
    private final QuestionnaireModelRepository questionnaireModelRepository;
    private final NomenclatureService nomenclatureService;

    public List<String> getQuestionnaireIds(String campaignId) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        return questionnaireModelRepository.findAllIds(campaignId);
    }

    @Cacheable(CacheName.QUESTIONNAIRE)
    public String getQuestionnaireData(String id) {
        return questionnaireModelRepository
                .findQuestionnaireData(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Questionnaire data %s was not found", id)));
    }

    @Transactional
    public void createQuestionnaire(QuestionnaireModel questionnaire) {
        questionnaireModelExistenceService.throwExceptionIfQuestionnaireAlreadyExist(questionnaire.id());

        if (!nomenclatureService.areNomenclaturesValid(questionnaire.requiredNomenclatureIds())) {
            throw new QuestionnaireModelServiceException(String.format("Cannot create questionnaire model %s as some nomenclatures do not exist",
                    questionnaire.id()));
        }

        questionnaireModelRepository.create(questionnaire);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheName.QUESTIONNAIRE_NOMENCLATURES, key = "#questionnaire.id"),
            @CacheEvict(value = CacheName.QUESTIONNAIRE_METADATA, key = "#questionnaire.id"),
            @CacheEvict(value = CacheName.QUESTIONNAIRE, key = "#questionnaire.id"),
    })
    @Transactional
    public void updateQuestionnaire(QuestionnaireModel questionnaire) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(questionnaire.campaignId());
        questionnaireModelExistenceService.throwExceptionIfQuestionnaireNotExist(questionnaire.id());

        if (!nomenclatureService.areNomenclaturesValid(questionnaire.requiredNomenclatureIds())) {
            throw new QuestionnaireModelServiceException(String.format("Cannot update questionnaire model %s as some nomenclatures do not exist",
                    questionnaire.id()));
        }
        questionnaireModelRepository.update(questionnaire);
    }

    public List<String> getQuestionnaireDatas(String campaignId) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        return questionnaireModelRepository.findAllQuestionnaireValues(campaignId).stream()
                .map(String::new).toList();
    }
}