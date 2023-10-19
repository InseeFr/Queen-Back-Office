package fr.insee.queen.api.service;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.Metadata;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.input.CampaignInputDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelIdDto;
import fr.insee.queen.api.exception.CampaignCreationException;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class CampaignService {
	private final ParadataEventRepository paradataEventRepository;
	private final SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;
	private final CampaignRepository campaignRepository;
	private final SurveyUnitRepository surveyUnitRepository;
    private final QuestionnaireModelRepository questionnaireModelRepository;
	private final QuestionnaireModelService questionnaireModelService;
	private final CacheManager cacheManager;

	public Campaign getCampaign(String campaignId) {
		// not using @Cacheable annotation here, to avoid problems with proxy class generation
		Campaign campaign = Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN)).get(campaignId, Campaign.class);
		if(campaign != null) {
			return campaign;
		}

		campaign = campaignRepository.findWithQuestionnairesById(campaignId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Campaign %s was not found", campaignId)));

		Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN)).putIfAbsent(campaignId, campaign);
		return campaign;
	}

	public void checkExistence(String campaignId) {
		if(!existsById(campaignId)) {
			throw new EntityNotFoundException(String.format("Campaign %s was not found", campaignId));
		}
	}

	private boolean existsById(String campaignId) {
		// not using @Cacheable annotation here, to avoid problems with proxy class generation
		Boolean isCampaignPresent = Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN_EXIST)).get(campaignId, Boolean.class);
		if(isCampaignPresent != null) {
			return isCampaignPresent;
		}
		isCampaignPresent = campaignRepository.existsById(campaignId);
		Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN_EXIST)).putIfAbsent(campaignId, isCampaignPresent);

		return isCampaignPresent;
	}

	public List<CampaignSummaryDto> getAllCampaigns() {
		List<Campaign> campaigns = campaignRepository.findAll();
		return campaigns.stream()
				.map(camp -> new CampaignSummaryDto(
						camp.id(),
						questionnaireModelService.findAllQuestionnaireIdDtoByCampaignId(camp.id())))
				.toList();
	}

	public List<QuestionnaireModelIdDto> getQuestionnaireIds(String campaignId) {
		Campaign campaign = getCampaign(campaignId);
		return campaign.questionnaireModels().stream()
				.map(q -> new QuestionnaireModelIdDto(q.id())).toList();
	}

	public List<QuestionnaireModelDto> getQuestionnaireModels(String campaignId) {
		Campaign campaign = getCampaign(campaignId);
		return campaign.questionnaireModels().stream()
				.map(q -> new QuestionnaireModelDto(q.value())).toList();
	}

	@Transactional
	@Caching(evict = {
			@CacheEvict(CacheName.CAMPAIGN),
			@CacheEvict(CacheName.CAMPAIGN_NOMENCLATURES),
			@CacheEvict(CacheName.METADATA),
			@CacheEvict(CacheName.CAMPAIGN_EXIST)
	})
	public void delete(String campaignId) {
		checkExistence(campaignId);
		paradataEventRepository.deleteParadataEvents(campaignId);
		surveyUnitTempZoneRepository.deleteSurveyUnits(campaignId);
		surveyUnitRepository.deleteSurveyUnits(campaignId);

		List<QuestionnaireModel> qmList = questionnaireModelService.findQuestionnaireModelByCampaignId(campaignId);

		if(qmList!=null && !qmList.isEmpty()) {
			questionnaireModelRepository.deleteAll(qmList);
			qmList.forEach(questionnaireModel -> {
				Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES))
						.evict(questionnaireModel.id());
				Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE))
						.evict(questionnaireModel.id());
				Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_EXIST))
						.evict(questionnaireModel.id());
			});
		}
		campaignRepository.deleteById(campaignId);
	}

	@Transactional
	public void createCampaign(CampaignInputDto campaignInputDto) {
		String campaignId = campaignInputDto.id().toUpperCase();

		if (existsById(campaignId)) {
			throw new CampaignCreationException(String.format("Campaign %s already exists. Creation aborted", campaignId));
		}

		List<String> questionnaireIds = campaignInputDto.questionnaireIds().stream().toList();
		List<QuestionnaireModel> questionnaireModels = questionnaireModelService.findByIds(questionnaireIds);

		if(questionnaireIds.size() != questionnaireModels.size()) {
			throw new CampaignCreationException(
					String.format("One or more questionnaires do not exist for campaign %s. Creation aborted.", campaignId));
		}
		// check that questionnaire models exist and are not already associated with a campaign
		boolean canQuestionnairesBeAssociated = questionnaireModels.stream()
				.allMatch(questionnaireModel -> questionnaireModel.campaign() != null);

		if(!canQuestionnairesBeAssociated) {
			throw new CampaignCreationException(
					String.format("One or more questionnaires are already associated for campaign %s. Creation aborted.", campaignId));
		}

		Campaign campaign = new Campaign(campaignId, campaignInputDto.label(), new HashSet<>(questionnaireModels));
		questionnaireModels.parallelStream()
				.forEach(questionnaireModel -> questionnaireModel.campaign(campaign));

		if (campaignInputDto.metadata() != null) {
			Metadata m = new Metadata(UUID.randomUUID(), campaignInputDto.metadata().value().toString(), campaign);
			campaign.metadata(m);
		}
		campaignRepository.save(campaign);
	}

	@Cacheable(CacheName.CAMPAIGN_NOMENCLATURES)
	public List<String> findRequiredNomenclatureByCampaign(String campaignId) {
		checkExistence(campaignId);
		return questionnaireModelRepository.findRequiredNomenclatureByCampaignId(campaignId);
	}
}
