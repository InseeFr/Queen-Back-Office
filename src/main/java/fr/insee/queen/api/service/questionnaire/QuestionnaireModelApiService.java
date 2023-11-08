package fr.insee.queen.api.service.questionnaire;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelData;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelValueDto;
import fr.insee.queen.api.service.gateway.QuestionnaireModelRepository;
import fr.insee.queen.api.service.campaign.CampaignExistenceService;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.service.exception.QuestionnaireModelServiceException;
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

	public List<String> findAllQuestionnaireIdDtoByCampaignId(String campaignId) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		return questionnaireModelRepository.findAllIds(campaignId);
	}

	@Cacheable(CacheName.QUESTIONNAIRE)
	public QuestionnaireModelValueDto getQuestionnaireModelDto(String id) {
		return questionnaireModelRepository
				.findQuestionnaireValue(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Questionnaire model %s was not found", id)));
	}

	@Transactional
	public void createQuestionnaire(QuestionnaireModelData questionnaire) {
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
	public void updateQuestionnaire(QuestionnaireModelData questionnaire) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(questionnaire.campaignId());
		questionnaireModelExistenceService.throwExceptionIfQuestionnaireNotExist(questionnaire.id());

		if (!nomenclatureService.areNomenclaturesValid(questionnaire.requiredNomenclatureIds())) {
			throw new QuestionnaireModelServiceException(String.format("Cannot update questionnaire model %s as some nomenclatures do not exist",
					questionnaire.id()));
		}
		questionnaireModelRepository.update(questionnaire);
	}

	public List<QuestionnaireModelIdDto> getQuestionnaireIds(String campaignId) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		return questionnaireModelRepository.findAllIds(campaignId)
				.stream().map(QuestionnaireModelIdDto::new).toList();
	}

	public List<QuestionnaireModelValueDto> getQuestionnaireValues(String campaignId) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		return questionnaireModelRepository.findAllQuestionnaireValues(campaignId).stream()
				.map(QuestionnaireModelValueDto::new).toList();
	}
}
