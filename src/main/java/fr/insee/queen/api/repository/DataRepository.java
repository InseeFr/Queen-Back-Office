package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.dto.data.DataDto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
* DataRepository is the repository using to access to  Data table in DB
* 
* @author Claudel Benjamin
* 
*/
@Transactional
@Repository
public interface DataRepository extends JpaRepository<Data, UUID> {
	/**
	* This method retrieve the Data for a specific reporting_unit
	* 
	* @param id the id of reporting unit
	* @return {@link DataDto}
	*/
	Optional<DataDto> findBySurveyUnitId(String id);
}
