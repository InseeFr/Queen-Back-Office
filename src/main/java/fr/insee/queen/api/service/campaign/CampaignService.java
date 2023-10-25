package fr.insee.queen.api.service.campaign;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.input.CampaignInputDto;
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

@Service
@AllArgsConstructor
@Slf4j
public class CampaignService {
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
	@Caching(evict = {
			@CacheEvict(CacheName.CAMPAIGN_NOMENCLATURES),
			@CacheEvict(CacheName.METADATA_BY_QUESTIONNAIRE),
			@CacheEvict(CacheName.CAMPAIGN_EXIST)
	})
	public void delete(String campaignId) {
		paradataEventRepository.deleteParadataEvents(campaignId);
		surveyUnitTempZoneRepository.deleteSurveyUnits(campaignId);
		surveyUnitRepository.deleteSurveyUnits(campaignId);

		CampaignSummaryDto campaignSummaryDto = campaignRepository.findWithQuestionnaireIds(campaignId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Campaign %s not found", campaignId)));

		List<String> questionnaireIds = campaignSummaryDto.questionnaireIds();

		if(questionnaireIds != null && !questionnaireIds.isEmpty()) {
			questionnaireModelRepository.deleteAllFromCampaign(campaignId);
			questionnaireIds.forEach(id -> {
				Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES))
						.evict(id);
				Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE))
						.evict(id);
				Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_EXIST))
						.evict(id);
			});
		}
		campaignRepository.deleteById(campaignId);
	}

	@Transactional
	public void createCampaign(CampaignInputDto campaign) {
		String campaignId = campaign.id();

		List<String> questionnaireIds = campaign.questionnaireIds().stream().toList();
		campaignExistenceService.throwExceptionIfCampaignAlreadyExist(campaignId);
		throwExceptionIfInvalidQuestionnairesBeforeSave(campaignId, questionnaireIds);

		String metadataValue = null;
		if(campaign.metadata() != null) {
			metadataValue = campaign.metadata().value().toString();
		}

		campaignRepository.createCampaign(campaignId, campaign.label(), questionnaireIds, metadataValue);
	}

	public void updateCampaign(CampaignInputDto campaign) {
		String campaignId = campaign.id();
		List<String> questionnaireIds = campaign.questionnaireIds().stream().toList();
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		throwExceptionIfInvalidQuestionnairesBeforeSave(campaignId, questionnaireIds);

		String metadataValue = null;
		if(campaign.metadata() != null) {
			metadataValue = campaign.metadata().value().toString();
		}

		campaignRepository.updateCampaign(campaign.id(), campaign.label(), questionnaireIds, metadataValue);
	}

	private void throwExceptionIfInvalidQuestionnairesBeforeSave(String campaignId, List<String> questionnaireIds) {
		Long nbValidQuestionnaires = questionnaireModelRepository.countValidQuestionnaires(campaignId, questionnaireIds);
		if(questionnaireIds.size() != nbValidQuestionnaires) {
			throw new CampaignServiceException(
					String.format("One or more questionnaires do not exist for campaign %s or are already associated with another campaign. Creation aborted.", campaignId));
		}
	}
}
