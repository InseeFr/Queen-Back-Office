package fr.insee.queen.api.service.questionnaire;

import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;

import java.util.List;
import java.util.Set;

public interface NomenclatureService {
	NomenclatureDto getNomenclature(String id);
	boolean existsById(String id);
	boolean areNomenclaturesValid(Set<String> nomenclatureIds);
	void saveNomenclature(NomenclatureInputDto nomenclature);
	List<String> getAllNomenclatureIds();
	List<String> findRequiredNomenclatureByCampaign(String campaignId);
	List<String> findRequiredNomenclatureByQuestionnaire(String questionnaireId);
}
