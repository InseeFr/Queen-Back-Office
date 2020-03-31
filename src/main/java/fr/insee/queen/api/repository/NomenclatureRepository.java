package fr.insee.queen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;

/**
* NomenclatureRepository is the repository using to access to Nomenclature table in DB
* 
* @author Claudel Benjamin
* 
*/
@Transactional
@Repository
public interface NomenclatureRepository extends JpaRepository<Nomenclature, String>{
	/**
	* This method retrieve the NomenclatureDto by id
	* 
	* @param id id of the operation
	* @return{@link NomenclatureDto}
	*/
	public NomenclatureDto findDtoById(String id);
	/**
	* This method retrieve all Nomenclature ids for a specific operation
	* 
	* @param id id of the operation
	* @return List of {@link String}
	*/
	@Query(value="SELECT rn.code " 
			+ "FROM required_nomenclature rn "
			+ "JOIN questionnaire_model qm ON qm.id = rn.id_required_nomenclature "
			+ "JOIN operation op ON op.questionnaire_model_id = qm.id "
			+ "WHERE op.id=?1 ", nativeQuery=true)
	public List<String> findRequiredNomenclatureByOperation(String id);
}
