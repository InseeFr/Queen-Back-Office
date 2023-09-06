package fr.insee.queen.api.service;

import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.input.QuestionnaireModelInputDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelCampaignDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class QuestionnaireModelService {

	private final QuestionnaireModelRepository questionnaireModelRepository;

	private final NomenclatureService nomenclatureService;

	public Optional<QuestionnaireModelCampaignDto> findById(String id) {
		return questionnaireModelRepository.findQuestionnaireModelWithCampaignById(id);
	}

	public List<QuestionnaireModel> findByIds(List<String> ids) {
		return questionnaireModelRepository.findAllById(ids);
	}

	public boolean existsById(String id) {
		return questionnaireModelRepository.existsById(id);
	}

	public List<String> findAllQuestionnaireIdDtoByCampaignId(String id) {
		return questionnaireModelRepository.findAllIdByCampaignId(id);
	}

	public QuestionnaireModelDto getQuestionnaireModelDto(String id) {
		return questionnaireModelRepository
				.findQuestionnaireModelById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Questionnaire model %s was not found", id)));
	}
	
	public List<QuestionnaireModel> findQuestionnaireModelByCampaignId(String id) {
		return questionnaireModelRepository.findByCampaignId(id);
	}

	public void save(QuestionnaireModel qm) {
		questionnaireModelRepository.save(qm);
	}

	public void createQuestionnaire(QuestionnaireModelInputDto qm) {
		QuestionnaireModel questionnaireModel = new QuestionnaireModel();
		questionnaireModel.id(qm.idQuestionnaireModel());
		questionnaireModel.label(qm.label());
		questionnaireModel.value(qm.value().toString());
		questionnaireModel.nomenclatures(nomenclatureService.findAllByIds(qm.requiredNomenclatureIds()));
		questionnaireModelRepository.save(questionnaireModel);
	}
}
