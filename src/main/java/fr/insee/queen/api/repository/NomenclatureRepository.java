package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
public interface NomenclatureRepository extends JpaRepository<Nomenclature, String>{
	Optional<NomenclatureDto> findNomenclatureById(String id);

	@Query("select n.id from Nomenclature n order by n.id asc")
	Optional<List<String>> findAllNomenclatureIds();

	Set<Nomenclature> findAllByIdIn(Set<String> ids);
}
