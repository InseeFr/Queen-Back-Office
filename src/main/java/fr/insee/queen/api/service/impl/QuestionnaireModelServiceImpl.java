package fr.insee.queen.api.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireIdDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelCreateDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelDto;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import fr.insee.queen.api.service.AbstractService;
import fr.insee.queen.api.service.NomenclatureService;
import fr.insee.queen.api.service.QuestionnaireModelService;

@Service
@Transactional
public class QuestionnaireModelServiceImpl extends AbstractService<QuestionnaireModel, String> implements QuestionnaireModelService {

    protected final QuestionnaireModelRepository questionnaireModelRepository;
    
    protected final NomenclatureService nomenclatureService;

    @Autowired
    public QuestionnaireModelServiceImpl(QuestionnaireModelRepository repository, NomenclatureService nomenclatureService) {
        this.questionnaireModelRepository = repository;
        this.nomenclatureService = nomenclatureService;
    }

    @Override
    protected JpaRepository<QuestionnaireModel, String> getRepository() {
        return questionnaireModelRepository;
    }

	@Override
	public Optional<QuestionnaireModel> findById(String id) {
		return questionnaireModelRepository.findById(id);
	}

	@Override
	public Optional<QuestionnaireModelDto> findQuestionnaireModelDtoByCampaignId(String id) {
		return questionnaireModelRepository.findDtoByCampaignId(id);
	}

	@Override
	public Optional<QuestionnaireIdDto> findQuestionnaireIdDtoByCampaignId(String id) {
		return questionnaireModelRepository.findIdByCampaignId(id);
	}

	public List<String> findAllQuestionnaireIdDtoByCampaignId(String id) {
		return questionnaireModelRepository.findAllIdByCampaignId(id);
	}

	@Override
	public Optional<QuestionnaireModelDto> findDtoById(String id) {
		return questionnaireModelRepository.findDtoById(id);
	}
	
	@Override
	public List<QuestionnaireModel> findQuestionnaireModelByCampaignId(String id) {
		return questionnaireModelRepository.findByCampaignId(id);
	}

	@Override
	public void save(QuestionnaireModel qm) {
		questionnaireModelRepository.save(qm);
	}
	
	@Override
	public void createQuestionnaire(QuestionnaireModelCreateDto qm) {
		QuestionnaireModel questionnaireModel = new QuestionnaireModel();
		questionnaireModel.setId(qm.getIdQuestionnaireModel());
		questionnaireModel.setLabel(qm.getLabel());
		questionnaireModel.setValue(qm.getValue());
		questionnaireModel.setNomenclatures(nomenclatureService.findAllByIds(qm.getRequiredNomenclatureIds()));
		questionnaireModelRepository.save(questionnaireModel);
	}
}
