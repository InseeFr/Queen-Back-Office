package fr.insee.queen.api.service.questionnaire;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.input.QuestionnaireModelInputDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelValueDto;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import fr.insee.queen.api.service.campaign.CampaignExistenceService;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.service.exception.QuestionnaireModelServiceException;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionnaireModelService {

	private final CampaignExistenceService campaignExistenceService;
	private final QuestionnaireModelExistenceService questionnaireModelExistenceService;
	private final QuestionnaireModelRepository questionnaireModelRepository;
	private final NomenclatureService nomenclatureService;
	private final CacheManager cacheManager;

	public List<String> findAllQuestionnaireIdDtoByCampaignId(String campaignId) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		return questionnaireModelRepository.findAllIdByCampaignId(campaignId);
	}

	@Cacheable(CacheName.QUESTIONNAIRE)
	public QuestionnaireModelValueDto getQuestionnaireModelDto(String id) {
		return questionnaireModelRepository
				.findQuestionnaireModelById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Questionnaire model %s was not found", id)));
	}

	@Transactional
	public void createQuestionnaire(QuestionnaireModelInputDto qm) {
		createQuestionnaire(qm, null);
	}

	@Transactional
	public void createQuestionnaire(QuestionnaireModelInputDto qm, String campaignId) {
		questionnaireModelExistenceService.throwExceptionIfQuestionnaireAlreadyExist(qm.idQuestionnaireModel());

		if (!nomenclatureService.areNomenclaturesValid(qm.requiredNomenclatureIds())) {
			throw new QuestionnaireModelServiceException(String.format("Cannot create questionnaire model %s as some nomenclatures do not exist",
					qm.idQuestionnaireModel()));
		}
		questionnaireModelRepository.createQuestionnaire(qm.idQuestionnaireModel(), qm.label(), qm.value().toString(), qm.requiredNomenclatureIds(), campaignId);
	}

	@Caching(evict = {
			@CacheEvict(value = CacheName.QUESTIONNAIRE_NOMENCLATURES, key = "#qm.idQuestionnaireModel"),
			@CacheEvict(value = CacheName.METADATA_BY_QUESTIONNAIRE, key = "#qm.idQuestionnaireModel"),
			@CacheEvict(value = CacheName.QUESTIONNAIRE, key = "#qm.idQuestionnaireModel"),
	})
	@Transactional
	public void updateQuestionnaire(QuestionnaireModelInputDto qm, String campaignId) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		questionnaireModelExistenceService.throwExceptionIfQuestionnaireNotExist(qm.idQuestionnaireModel());

		if (!nomenclatureService.areNomenclaturesValid(qm.requiredNomenclatureIds())) {
			throw new QuestionnaireModelServiceException(String.format("Cannot update questionnaire model %s as some nomenclatures do not exist",
					qm.idQuestionnaireModel()));
		}
		questionnaireModelRepository.updateQuestionnaire(qm.idQuestionnaireModel(), qm.label(), qm.value().toString(), qm.requiredNomenclatureIds(), campaignId);
	}

	public List<QuestionnaireModelIdDto> getQuestionnaireIds(String campaignId) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		return questionnaireModelRepository.findAllIdByCampaignId(campaignId)
				.stream().map(QuestionnaireModelIdDto::new).toList();
	}

	public List<QuestionnaireModelValueDto> getQuestionnaireValues(String campaignId) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		return questionnaireModelRepository.findAllValueByCampaignId(campaignId).stream()
				.map(QuestionnaireModelValueDto::new).toList();
	}
}
