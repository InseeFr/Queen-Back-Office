package fr.insee.queen.api.service.questionnaire;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.service.gateway.NomenclatureRepository;
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
		return nomenclatureRepository.find(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Nomenclature %s was not found", id)));
	}

	public boolean existsById(String id) {
		return nomenclatureRepository.exists(id);
	}

	public boolean areNomenclaturesValid(Set<String> nomenclatureIds) {
		if(nomenclatureIds.isEmpty()) {
			return true;
		}
		return nomenclatureIds.stream().allMatch(nomenclatureRepository::exists);
	}

	@CacheEvict(value = CacheName.NOMENCLATURE, key = "#nomenclature.id")
	public void saveNomenclature(NomenclatureInputDto nomenclature) {
		if(nomenclatureRepository.exists(nomenclature.id())) {
			log.info("Update nomenclature: " + nomenclature.id());
			nomenclatureRepository.update(nomenclature.id(), nomenclature.label(), nomenclature.value().toString());
			return;
		}

		log.info("Create new nomenclature: " + nomenclature.id());
		nomenclatureRepository.create(nomenclature.id(), nomenclature.label(), nomenclature.value().toString());
	}

	public List<String> getAllNomenclatureIds() {
		return nomenclatureRepository.findAllIds()
				.orElseThrow(() -> new EntityNotFoundException("No nomenclatures found"));
	}

	public List<String> findRequiredNomenclatureByCampaign(String campaignId) {
		campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
		return nomenclatureRepository.findRequiredNomenclatureByCampaignId(campaignId);
	}

	@Cacheable(CacheName.QUESTIONNAIRE_NOMENCLATURES)
	public List<String> findRequiredNomenclatureByQuestionnaire(String questionnaireId){
		questionnaireModelExistenceService.throwExceptionIfQuestionnaireNotExist(questionnaireId);
		return nomenclatureRepository.findRequiredNomenclatureByQuestionnaireId(questionnaireId);
	}
}
