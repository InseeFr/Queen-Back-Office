package fr.insee.queen.api.service;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.input.QuestionnaireModelInputDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.exception.QuestionnaireModelCreationException;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class QuestionnaireModelService {

	private final QuestionnaireModelRepository questionnaireModelRepository;

	private final NomenclatureService nomenclatureService;

	private final CacheManager cacheManager;

	private boolean existsById(String questionnaireId) {
		// not using @Cacheable annotation here, to avoid problems with proxy class generation
		Boolean isQuestionnairePresent = Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_EXIST)).get(questionnaireId, Boolean.class);
		if(isQuestionnairePresent != null) {
			return isQuestionnairePresent;
		}
		isQuestionnairePresent = questionnaireModelRepository.existsById(questionnaireId);
		Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_EXIST)).putIfAbsent(questionnaireId, isQuestionnairePresent);
		return isQuestionnairePresent;
	}

	public List<QuestionnaireModel> findByIds(List<String> ids) {
		return questionnaireModelRepository.findAllById(ids);
	}

	public List<String> findAllQuestionnaireIdDtoByCampaignId(String id) {
		return questionnaireModelRepository.findAllIdByCampaignId(id);
	}

	@Cacheable(CacheName.QUESTIONNAIRE)
	public QuestionnaireModelDto getQuestionnaireModelDto(String id) {
		return questionnaireModelRepository
				.findQuestionnaireModelById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Questionnaire model %s was not found", id)));
	}
	
	public List<QuestionnaireModel> findQuestionnaireModelByCampaignId(String id) {
		return questionnaireModelRepository.findByCampaignId(id);
	}

	@Transactional
	public void createQuestionnaire(QuestionnaireModelInputDto qm) {
		if (existsById(qm.idQuestionnaireModel())) {
			throw new QuestionnaireModelCreationException(String.format("Cannot create questionnaire model %s as it already exists",
					qm.idQuestionnaireModel()));
		}

		if (!nomenclatureService.areNomenclaturesValid(qm.requiredNomenclatureIds())) {
			throw new QuestionnaireModelCreationException(String.format("Cannot create questionnaire model %s as some nomenclatures do not exist",
					qm.idQuestionnaireModel()));
		}
		QuestionnaireModel questionnaireModel = new QuestionnaireModel();
		questionnaireModel.id(qm.idQuestionnaireModel());
		questionnaireModel.label(qm.label());
		questionnaireModel.value(qm.value().toString());
		questionnaireModel.nomenclatures(nomenclatureService.findAllByIds(qm.requiredNomenclatureIds()));
		questionnaireModelRepository.save(questionnaireModel);
	}

	@Cacheable(CacheName.QUESTIONNAIRE_NOMENCLATURES)
	public List<String> findRequiredNomenclatureByQuestionnaire(String questionnaireId){
		QuestionnaireModel questionnaireModel = questionnaireModelRepository.findById(questionnaireId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Questionnaire model %s was not found", questionnaireId)));
		List<String> requiredNomenclatureIds =  questionnaireModelRepository.findRequiredNomenclatureByQuestionnaireId(questionnaireModel.id());
		if(requiredNomenclatureIds.isEmpty()) {
			throw new EntityNotFoundException(String.format("No required nomenclatures found for questionnaire %s", questionnaireId));
		}
		return requiredNomenclatureIds;
	}
}
