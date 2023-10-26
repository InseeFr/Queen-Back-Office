package fr.insee.queen.api.repository;

import fr.insee.queen.api.entity.NomenclatureDB;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
* NomenclatureRepository is the repository using to access to Nomenclature table in DB
* 
* @author Claudel Benjamin
* 
*/
@Repository
public interface NomenclatureRepository extends JpaRepository<NomenclatureDB, String>{
	Optional<NomenclatureDto> findNomenclatureById(String id);

	@Query("select n.id from NomenclatureDB n order by n.id asc")
	Optional<List<String>> findAllNomenclatureIds();

	Set<NomenclatureDB> findAllByIdIn(Set<String> ids);

	@Transactional
	@Modifying
	@Query("update NomenclatureDB n set n.label=:label, n.value=:value where n.id = :id")
	void updateNomenclature(String id, String label, String value);

	@Transactional
	@Modifying
	@Query(value = """
		INSERT INTO nomenclature (id, label, value)
		VALUES (:id, :label, :value\\:\\:jsonb)""", nativeQuery = true)
	void createNomenclature(String id, String label, String value);

	@Query("""
	    select distinct n.id from QuestionnaireModelDB qm inner join qm.nomenclatures n where qm.campaign.id=:campaignId
	""")
	List<String> findRequiredNomenclatureByCampaignId(String campaignId);

	@Query("""
	    select n.id from QuestionnaireModelDB qm inner join qm.nomenclatures n where qm.id=:questionnaireId
	""")
	List<String> findRequiredNomenclatureByQuestionnaireId(String questionnaireId);
}
