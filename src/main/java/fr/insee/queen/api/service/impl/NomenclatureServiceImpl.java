package fr.insee.queen.api.service.impl;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.exception.NotFoundException;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.NomenclatureRepository;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import fr.insee.queen.api.service.AbstractService;
import fr.insee.queen.api.service.NomenclatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class NomenclatureServiceImpl extends AbstractService<Nomenclature, String> implements NomenclatureService {
	
    protected final NomenclatureRepository nomenclatureRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(NomenclatureServiceImpl.class);

    @Autowired
    private CampaignRepository campaignRepository; 
    @Autowired
    private QuestionnaireModelRepository questionnaireModelRepository;

    @Autowired
    public NomenclatureServiceImpl(NomenclatureRepository repository) {
        this.nomenclatureRepository = repository;
    }
    
    @Override
    protected JpaRepository<Nomenclature, String> getRepository() {
        return nomenclatureRepository;
    }

	@Override
	@Cacheable("nomenclature")
	public Optional<Nomenclature> findById(String id) {
		return nomenclatureRepository.findById(id);
	}

	@Override
	public List<String> findRequiredNomenclatureByQuestionnaire(String questionnaireModelId){
		Optional<QuestionnaireModel> q = questionnaireModelRepository.findById(questionnaireModelId);
		if(!q.isPresent()) {
			return null;
		}
		return q.get().getNomenclatures().parallelStream().distinct().map(Nomenclature::getId).collect(Collectors.toList());
	}

	@Cacheable("required-nomenclatures")
	public List<String> findRequiredNomenclatureByCampaign(String campaignId) throws NotFoundException {
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
	public void createNomenclature(NomenclatureDto nomenclature) {
    	if(nomenclatureRepository.findById(nomenclature.getId()).isPresent()){
			Optional<Nomenclature> nomemclatureAlreadyOnDB = nomenclatureRepository.findById(nomenclature.getId());
			LOGGER.info("Update nomenclature" + nomenclature.getId());

			Nomenclature nomencla = nomemclatureAlreadyOnDB.get() ;
			nomencla.setValue(nomenclature.getValue());
			nomenclatureRepository.save(nomencla);
		}
    	else{
			Nomenclature newNomenclature = new Nomenclature(nomenclature.getId(), nomenclature.getLabel(), nomenclature.getValue());
			LOGGER.info("Create new nomenclature" + nomenclature.getId());
			nomenclatureRepository.save(newNomenclature);
		}

	}


	@Override
	public void delete(Nomenclature nomenclature) {
		nomenclatureRepository.delete(nomenclature);
	}

	@Override
	public List<Nomenclature> findAll() {
		return nomenclatureRepository.findAll();
	}
}
