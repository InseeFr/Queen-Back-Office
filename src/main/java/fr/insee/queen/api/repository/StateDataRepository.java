package fr.insee.queen.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.dto.data.DataDto;
import fr.insee.queen.api.dto.stateData.StateDataDto;

/**
* DataRepository is the repository using to access to  Data table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface StateDataRepository extends JpaRepository<StateData, Long> {
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
	Optional<StateDataDto> findDtoBySurveyUnit_id(String id);
	/**
	* This method retrieve the Data for a specific reporting_unit
	* 
	* @param id the id of reporting unit
	* @return {@link Data}
	*/
	Optional<StateData> findBySurveyUnit_id(String id);
}
