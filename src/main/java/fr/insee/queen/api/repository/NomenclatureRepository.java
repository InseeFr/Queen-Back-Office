package fr.insee.queen.api.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
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
	* @param id id of the campaign
	* @return{@link NomenclatureDto}
	*/
	public NomenclatureDto findDtoById(String id);
	
	public Optional<Nomenclature> findById(String id);

}
