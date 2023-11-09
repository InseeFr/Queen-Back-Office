package fr.insee.queen.api.service.campaign;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.domain.CampaignData;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.repository.*;
import fr.insee.queen.api.service.exception.CampaignServiceException;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
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
	private final ParadataEventRepository paradataEventRepository;
	private final SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;
	private final CampaignRepository campaignRepository;
	private final SurveyUnitRepository surveyUnitRepository;
    private final QuestionnaireModelRepository questionnaireModelRepository;
	private final CampaignExistenceService campaignExistenceService;
	private final CacheManager cacheManager;

	public List<CampaignSummaryDto> getAllCampaigns() {
		return campaignRepository.findAllWithQuestionnaireIds();
	}

	@Transactional
	@CacheEvict(CacheName.CAMPAIGN_EXIST)
	@Override
	public void delete(String campaignId) {
		paradataEventRepository.deleteParadataEvents(campaignId);
		surveyUnitTempZoneRepository.deleteSurveyUnits(campaignId);
		surveyUnitRepository.deleteSurveyUnits(campaignId);

		CampaignSummaryDto campaignSummaryDto = campaignRepository.findWithQuestionnaireIds(campaignId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Campaign %s not found", campaignId)));

		List<String> questionnaireIds = campaignSummaryDto.questionnaireIds();

		if(questionnaireIds != null && !questionnaireIds.isEmpty()) {
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
		campaignRepository.deleteById(campaignId);
	}

	@Transactional
	@Caching(evict = {
			@CacheEvict(value = CacheName.CAMPAIGN_EXIST, key = "#campaign.id")
	})
	@Override
	public void createCampaign(CampaignData campaign) {
		String campaignId = campaign.id();
		campaignExistenceService.throwExceptionIfCampaignAlreadyExist(campaignId);
		throwExceptionIfInvalidQuestionnairesBeforeSave(campaign.id(), campaign.questionnaireIds());
		campaignRepository.createCampaign(campaign);
	}

	@Caching(evict = {
			@CacheEvict(value = CacheName.QUESTIONNAIRE_METADATA, allEntries = true),
	})
	@Override
	public void updateCampaign(CampaignData campaign) {
		String campaignId = campaign.id();
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		throwExceptionIfInvalidQuestionnairesBeforeSave(campaignId, campaign.questionnaireIds());
		campaignRepository.updateCampaign(campaign);
	}

	private void throwExceptionIfInvalidQuestionnairesBeforeSave(String campaignId, Set<String> questionnaireIds) {
		Long nbValidQuestionnaires = questionnaireModelRepository.countValidQuestionnaires(campaignId, questionnaireIds);
		if(questionnaireIds.size() != nbValidQuestionnaires) {
			throw new CampaignServiceException(
					String.format("One or more questionnaires do not exist for campaign %s or are already associated with another campaign. Creation aborted.", campaignId));
		}
	}
}
