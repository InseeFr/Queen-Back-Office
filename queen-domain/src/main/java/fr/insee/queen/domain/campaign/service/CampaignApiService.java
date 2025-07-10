package fr.insee.queen.domain.campaign.service;

import fr.insee.queen.domain.campaign.service.exception.QuestionnaireInvalidException;
import fr.insee.queen.domain.campaign.gateway.CampaignRepository;
import fr.insee.queen.domain.campaign.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class CampaignApiService implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final InterrogationRepository interrogationRepository;
    private final QuestionnaireModelRepository questionnaireModelRepository;
    private final CampaignExistenceService campaignExistenceService;
    private final CacheManager cacheManager;

    @Override
    public Campaign getCampaign(String campaignId) {
        return campaignRepository.findCampaign(campaignId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Campaign %s not found", campaignId)));
    }

    @Override
    public List<String> getAllCampaignIds() {
        return campaignRepository.getAllCampaignIds();
    }

    public List<CampaignSummary> getAllCampaigns() {
        return campaignRepository.getAllWithQuestionnaireIds();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(CacheName.CAMPAIGN_EXIST),
            @CacheEvict(value = CacheName.INTERROGATION_EXIST, allEntries = true),
            @CacheEvict(value = CacheName.INTERROGATION_SUMMARY, allEntries = true)
    })
    @Override
    public void delete(String campaignId) {
        interrogationRepository.deleteInterrogations(campaignId);

        CampaignSummary campaignSummary = campaignRepository.
                findWithQuestionnaireIds(campaignId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Campaign %s not found", campaignId)));

        Set<String> questionnaireIds = campaignSummary.getQuestionnaireIds();

        if (questionnaireIds != null && !questionnaireIds.isEmpty()) {
            questionnaireIds.forEach(id -> {
                Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES))
                        .evict(id);
                Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA))
                        .evict(id);
                Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE))
                        .evict(id);
            });
            questionnaireModelRepository.deleteAllFromCampaign(campaignId);
        }
        campaignRepository.delete(campaignId);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.CAMPAIGN_EXIST, key = "#campaign.id")
    })
    @Override
    public void createCampaign(Campaign campaign) {
        String campaignId = campaign.getId();
        campaignExistenceService.throwExceptionIfCampaignAlreadyExist(campaignId);
        throwExceptionIfInvalidQuestionnairesBeforeSave(campaign.getId(), campaign.getQuestionnaireIds());
        campaignRepository.create(campaign);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheName.QUESTIONNAIRE_METADATA, allEntries = true),
    })
    @Override
    public void updateCampaign(Campaign campaign) {
        String campaignId = campaign.getId();
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        throwExceptionIfInvalidQuestionnairesBeforeSave(campaignId, campaign.getQuestionnaireIds());
        campaignRepository.update(campaign);
    }

    private void throwExceptionIfInvalidQuestionnairesBeforeSave(String campaignId, Set<String> questionnaireIds) {
        Long nbValidQuestionnaires = questionnaireModelRepository.countValidQuestionnaires(campaignId, questionnaireIds);
        if (questionnaireIds.size() != nbValidQuestionnaires) {
            throw new QuestionnaireInvalidException(
                    String.format("One or more questionnaires do not exist for campaign %s or are already linked with another campaign. Creation aborted.", campaignId));
        }
    }
}
