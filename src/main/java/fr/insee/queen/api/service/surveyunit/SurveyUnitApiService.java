package fr.insee.queen.api.service.surveyunit;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.depositproof.PdfDepositProof;
import fr.insee.queen.api.dto.input.StateDataInputDto;
import fr.insee.queen.api.dto.input.SurveyUnitCreateInputDto;
import fr.insee.queen.api.dto.input.SurveyUnitUpdateInputDto;
import fr.insee.queen.api.dto.statedata.StateDataType;
import fr.insee.queen.api.dto.surveyunit.*;
import fr.insee.queen.api.service.campaign.CampaignExistenceService;
import fr.insee.queen.api.service.depositproof.PDFDepositProofService;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.service.gateway.StateDataRepository;
import fr.insee.queen.api.service.gateway.SurveyUnitRepository;
import fr.insee.queen.api.service.questionnaire.QuestionnaireModelApiExistenceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class SurveyUnitApiService implements SurveyUnitService {
	private final SurveyUnitRepository surveyUnitRepository;
	private final CampaignExistenceService campaignExistenceService;
	private final QuestionnaireModelApiExistenceService questionnaireModelExistenceService;
	private final StateDataRepository stateDataRepository;
	private final PDFDepositProofService pdfService;
	private final CacheManager cacheManager;

	@Override
	public boolean existsById(String surveyUnitId) {
		// not using @Cacheable annotation here, to avoid problems with proxy class generation
		Boolean isSurveyUnitPresent = Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId, Boolean.class);
		if(isSurveyUnitPresent != null) {
			return isSurveyUnitPresent;
		}
		isSurveyUnitPresent = surveyUnitRepository.exists(surveyUnitId);
		Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).putIfAbsent(surveyUnitId, isSurveyUnitPresent);

		return isSurveyUnitPresent;
	}

	public void throwExceptionIfSurveyUnitNotExist(String surveyUnitId) {
		if(!existsById(surveyUnitId)) {
			throw new EntityNotFoundException(String.format("Survey unit %s was not found", surveyUnitId));
		}
	}

	@Override
	public SurveyUnitDto getSurveyUnit(String id) {
		return surveyUnitRepository.find(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s was not found", id)));
	}

	@Override
	public List<SurveyUnitSummaryDto> findByCampaignId(String campaignId) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		return surveyUnitRepository.findAllSummaryByCampaignId(campaignId);
	}

	@Override
	public List<String> findAllSurveyUnitIds() {
		return surveyUnitRepository.findAllIds()
				.orElseThrow(() -> new EntityNotFoundException("List of survey unit ids not found"));
	}

	@Transactional
	@Override
	public void updateSurveyUnit(String surveyUnitId, SurveyUnitUpdateInputDto surveyUnit) {
		throwExceptionIfSurveyUnitNotExist(surveyUnitId);
		String personalization = surveyUnit.personalization() == null ? null : surveyUnit.personalization().toString();
		String comment = surveyUnit.comment() == null ? null : surveyUnit.comment().toString();
		String data = surveyUnit.data() == null ? null : surveyUnit.data().toString();
		surveyUnitRepository.update(surveyUnitId,
				personalization,
				comment,
				data,
				StateDataInputDto.toModel(surveyUnit.stateData()));
	}

	@Override
	public PdfDepositProof generateDepositProof(String userId, String surveyUnitId) {
		SurveyUnitDepositProofDto surveyUnit = getSurveyUnitDepositProof(surveyUnitId);
		String campaignId = surveyUnit.campaign().id();
		String campaignLabel = surveyUnit.campaign().label();
		String date = "";

		if (surveyUnit.stateData() == null) {
			throw new EntityNotFoundException(String.format("State data for survey unit %s was not found", surveyUnitId));
		}

		if(Arrays.asList(StateDataType.EXTRACTED,StateDataType.VALIDATED).contains(surveyUnit.stateData().state())) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm"); 
			date = dateFormat.format(new Date(surveyUnit.stateData().date()));
		}
		String filename = String.format("%s_%s.pdf", campaignId, userId);

		return new PdfDepositProof(filename, pdfService.retrievePdf(date,campaignLabel, userId));
	}

	@Transactional
	@Override
	@CacheEvict(value = CacheName.SURVEY_UNIT_EXIST, key = "#surveyUnit.id")
	public void createSurveyUnit(String campaignId, SurveyUnitCreateInputDto surveyUnit) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		questionnaireModelExistenceService.throwExceptionIfQuestionnaireNotExist(surveyUnit.questionnaireId());

		String surveyUnitId = surveyUnit.id();
		surveyUnitRepository.create(surveyUnitId, campaignId,
				surveyUnit.questionnaireId(),
				surveyUnit.data().toString(),
				surveyUnit.comment().toString(),
				surveyUnit.personalization().toString(),
				StateDataInputDto.toModel(surveyUnit.stateData()));
	}

	@Override
	public List<SurveyUnitSummaryDto> findSummaryByIds(List<String> surveyUnits) {
		return surveyUnitRepository.findAllSummaryByIdIn(surveyUnits);
	}

	@Override
	public Optional<SurveyUnitSummaryDto> findSummaryById(String surveyUnitId) {
		return surveyUnitRepository.findSummaryById(surveyUnitId);
	}

	@Override
	public List<SurveyUnitWithStateDto> findWithStateByIds(List<String> surveyUnits) {
		return surveyUnitRepository.findAllWithStateByIdIn(surveyUnits);
	}

	@Transactional
	@Caching(evict = {
			@CacheEvict(CacheName.SURVEY_UNIT_EXIST),
			@CacheEvict(CacheName.SURVEY_UNIT_CAMPAIGN)
	})
	public void delete(String surveyUnitId) {
		throwExceptionIfSurveyUnitNotExist(surveyUnitId);
		surveyUnitRepository.delete(surveyUnitId);
	}

	@Override
	public SurveyUnitDepositProofDto getSurveyUnitDepositProof(String surveyUnitId) {
		return surveyUnitRepository
				.findWithCampaignAndStateById(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s was not found", surveyUnitId)));
	}

	@Override
	@Cacheable(CacheName.SURVEY_UNIT_CAMPAIGN)
	public SurveyUnitHabilitationDto getSurveyUnitWithCampaignById(String surveyUnitId) {
		return surveyUnitRepository.findWithCampaignById(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s was not found", surveyUnitId)));
	}
}
