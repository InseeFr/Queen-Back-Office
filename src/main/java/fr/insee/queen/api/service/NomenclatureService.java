package fr.insee.queen.api.service;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.NomenclatureRepository;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class NomenclatureService {
	private final NomenclatureRepository nomenclatureRepository;

	private final CampaignRepository campaignRepository;

	private final QuestionnaireModelRepository questionnaireModelRepository;

	@Cacheable("nomenclatures")
	public NomenclatureDto getNomenclature(String id) {
		return nomenclatureRepository.findNomenclatureById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Nomenclature %s was not found", id)));
	}

	public List<String> findRequiredNomenclatureByQuestionnaire(String questionnaireId){
		QuestionnaireModel questionnaireModel = questionnaireModelRepository.findById(questionnaireId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Questionnaire model %s was not found", questionnaireId)));

		return questionnaireModel.nomenclatures()
				.parallelStream()
				.distinct()
				.map(Nomenclature::id)
				.toList();
	}

	@Cacheable("required-nomenclatures-campaign")
	public List<String> findRequiredNomenclatureByCampaign(String campaignId) {
		Campaign campaign = campaignRepository.findById(campaignId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Campaign %s was not found", campaignId)));

		Set<QuestionnaireModel> questionnaireModels = campaign.questionnaireModels();
		return questionnaireModels.parallelStream()
				.map(QuestionnaireModel::nomenclatures).toList()
				.parallelStream().flatMap(Set::parallelStream).toList()
				.parallelStream().distinct().map(Nomenclature::id).toList();
	}

	public boolean areNomenclaturesValid(Set<String> nomenclatureIds) {
		if(nomenclatureIds.isEmpty()) {
			return true;
		}
		return nomenclatureIds.stream().anyMatch(nomenclatureId ->
			nomenclatureRepository
					.findById(nomenclatureId)
					.isPresent()
		);
	}
	
	public Set<Nomenclature> findAllByIds(Set<String> nomenclatureIds){
		return nomenclatureRepository.findAllByIdIn(nomenclatureIds);
	}

	public void saveNomenclature(NomenclatureInputDto nomenclature) {
		Optional<Nomenclature> nomenclatureOptional = nomenclatureRepository.findById(nomenclature.id());

    	if(nomenclatureOptional.isPresent()){
			log.info("Update nomenclature" + nomenclature.id());
			Nomenclature nomenclatureDB = nomenclatureOptional.get();
			nomenclatureDB.value(nomenclature.value().toString());
			nomenclatureRepository.save(nomenclatureDB);
			return;
		}

		log.info("Create new nomenclature" + nomenclature.id());
		Nomenclature newNomenclature = new Nomenclature(nomenclature.id(), nomenclature.label(), nomenclature.value().toString());
		nomenclatureRepository.save(newNomenclature);
	}

	public List<String> getAllNomenclatureIds() {
		return nomenclatureRepository.findAllNomenclatureIds()
				.orElseThrow(() -> new EntityNotFoundException("No nomenclatures found"));
	}
}
