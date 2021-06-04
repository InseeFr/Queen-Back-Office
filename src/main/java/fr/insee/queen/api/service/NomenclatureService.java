package fr.insee.queen.api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;

public interface NomenclatureService extends BaseService<Nomenclature, String> {

	Optional<Nomenclature> findById(String id);

	void save(Nomenclature n);

	List<String> findRequiredNomenclatureByCampaign(String campaignId) throws Exception;
	
	public List<String> findRequiredNomenclatureByQuestionnaire(Set<QuestionnaireModel> setQuestionnaireModel);
	
	Boolean checkIfNomenclatureExists(Set<String> ids);

	Set<Nomenclature> findAllByIds(Set<String> nomenclatureIds);

	void createNomenclature(NomenclatureDto nomenclature) throws Exception;
    
}
