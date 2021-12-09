package fr.insee.queen.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.dto.data.DataDto;

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
	* This method retrieve all Data in DB
	* 
	* @return List of all {@link DataDto}
	*/
	List<DataDto> findDtoBy();
	/**
	* This method retrieve the Data for a specific reporting_unit
	* 
	* @param id the id of reporting unit
	* @return {@link DataDto}
	*/
	DataDto findDtoBySurveyUnitId(String id);
	/**
	* This method retrieve the Data for a specific reporting_unit
	* 
	* @param id the id of reporting unit
	* @return {@link Data}
	*/
	Optional<Data> findBySurveyUnitId(String id);
}
