package fr.insee.queen.api.service.questionnaire;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.repository.NomenclatureRepository;
import fr.insee.queen.api.service.campaign.CampaignExistenceService;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class NomenclatureApiService implements NomenclatureService {
	private final NomenclatureRepository nomenclatureRepository;
	private final CampaignExistenceService campaignExistenceService;
	private final QuestionnaireModelExistenceService questionnaireModelExistenceService;

	@Cacheable(CacheName.NOMENCLATURE)
	public NomenclatureDto getNomenclature(String id) {
		return nomenclatureRepository.findNomenclatureById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Nomenclature %s was not found", id)));
	}

	public boolean existsById(String id) {
		return nomenclatureRepository.existsById(id);
	}

	public boolean areNomenclaturesValid(Set<String> nomenclatureIds) {
		if(nomenclatureIds.isEmpty()) {
			return true;
		}
		return nomenclatureIds.stream().anyMatch(nomenclatureRepository::existsById);
	}

	@CacheEvict(value = CacheName.NOMENCLATURE, key = "#nomenclature.id")
	public void saveNomenclature(NomenclatureInputDto nomenclature) {
		if(nomenclatureRepository.existsById(nomenclature.id())) {
			log.info("Update nomenclature: " + nomenclature.id());
			nomenclatureRepository.updateNomenclature(nomenclature.id(), nomenclature.label(), nomenclature.value().toString());
			return;
		}

		log.info("Create new nomenclature: " + nomenclature.id());
		nomenclatureRepository.createNomenclature(nomenclature.id(), nomenclature.label(), nomenclature.value().toString());
	}

	public List<String> getAllNomenclatureIds() {
		return nomenclatureRepository.findAllNomenclatureIds()
				.orElseThrow(() -> new EntityNotFoundException("No nomenclatures found"));
	}

	public List<String> findRequiredNomenclatureByCampaign(String campaignId) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		return nomenclatureRepository.findRequiredNomenclatureByCampaignId(campaignId);
	}

	@Cacheable(CacheName.QUESTIONNAIRE_NOMENCLATURES)
	public List<String> findRequiredNomenclatureByQuestionnaire(String questionnaireId){
		questionnaireModelExistenceService.throwExceptionIfQuestionnaireNotExist(questionnaireId);
		List<String> requiredNomenclatureIds =  nomenclatureRepository.findRequiredNomenclatureByQuestionnaireId(questionnaireId);
		if(requiredNomenclatureIds.isEmpty()) {
			throw new EntityNotFoundException(String.format("No required nomenclatures found for questionnaire %s", questionnaireId));
		}
		return requiredNomenclatureIds;
	}
}
