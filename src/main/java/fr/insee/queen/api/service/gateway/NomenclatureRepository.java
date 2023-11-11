package fr.insee.queen.api.service.gateway;

import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.repository.entity.NomenclatureDB;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
* NomenclatureRepository is the repository using to access to Nomenclature table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface NomenclatureRepository {
	Optional<NomenclatureDto> find(String nomenclatureId);
	Optional<List<String>> findAllIds();
	Set<NomenclatureDB> find(Set<String> ids);
	void update(String id, String label, String value);
	void create(String id, String label, String value);
	List<String> findRequiredNomenclatureByCampaignId(String campaignId);
	List<String> findRequiredNomenclatureByQuestionnaireId(String questionnaireId);
	boolean exists(String nomenclatureId);
}
