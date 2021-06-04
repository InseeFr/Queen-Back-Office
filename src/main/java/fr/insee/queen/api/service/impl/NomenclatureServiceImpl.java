package fr.insee.queen.api.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.exception.NotFoundException;
import fr.insee.queen.api.repository.ApiRepository;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.NomenclatureRepository;
import fr.insee.queen.api.service.AbstractService;
import fr.insee.queen.api.service.NomenclatureService;

@Service
@Transactional
public class NomenclatureServiceImpl extends AbstractService<Nomenclature, String> implements NomenclatureService {
	
    protected final NomenclatureRepository nomenclatureRepository;
    
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    public NomenclatureServiceImpl(NomenclatureRepository repository) {
        this.nomenclatureRepository = repository;
    }
    
    @Override
    protected ApiRepository<Nomenclature, String> getRepository() {
        return nomenclatureRepository;
    }

	@Override
	public Optional<Nomenclature> findById(String id) {
		return nomenclatureRepository.findById(id);
	}

	@Override
	public List<String> findRequiredNomenclatureByQuestionnaire(Set<QuestionnaireModel> setQuestionnaireModel){
		return setQuestionnaireModel.parallelStream().map(QuestionnaireModel::getNomenclatures).collect(Collectors.toList())
				.parallelStream().flatMap(Set::parallelStream).collect(Collectors.toList())
				.parallelStream().distinct().map(Nomenclature::getId).collect(Collectors.toList());
	}
	
	public List<String> findRequiredNomenclatureByCampaign(String campaignId) throws Exception {
		Optional<Campaign> campaignOptional = campaignRepository.findById(campaignId);
		if (campaignOptional.isPresent()) {
			Set<QuestionnaireModel> setQuestionnaireModel = campaignOptional.get().getQuestionnaireModels();
			return setQuestionnaireModel.parallelStream().map(QuestionnaireModel::getNomenclatures).collect(Collectors.toList())
					.parallelStream().flatMap(Set::parallelStream).collect(Collectors.toList())
					.parallelStream().distinct().map(Nomenclature::getId).collect(Collectors.toList());
		} else {
			throw new NotFoundException("Campaign " + campaignId + "not found in database");
		}
	}

	@Override
	public void save(Nomenclature n) {
		nomenclatureRepository.save(n);
	}
	
	@Override
	public Boolean checkIfNomenclatureExists(Set<String> ids) {
		return ids.stream().anyMatch(nomenclature -> 
			nomenclatureRepository.findById(nomenclature).isPresent()
		);
	}
	
	@Override
	public Set<Nomenclature> findAllByIds(Set<String> nomenclatureIds){
		return nomenclatureIds.stream().map(id -> nomenclatureRepository.findById(id).get()).collect(Collectors.toSet());
	}

	@Override
	public void createNomenclature(NomenclatureDto nomenclature) throws Exception{
		Nomenclature newNomenclature = new Nomenclature(nomenclature.getId(), nomenclature.getLabel(), nomenclature.getValue());
		nomenclatureRepository.save(newNomenclature);
	}
}
