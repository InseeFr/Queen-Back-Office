package fr.insee.queen.api.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.dto.data.DataDto;

/**
* DataRepository is the repository using to access to  Data table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface StateDataRepository extends JpaRepository<StateData, UUID> {
	/**
	* This method retrieve the Data for a specific reporting_unit
	* 
	* @param id the id of reporting unit
	* @return {@link DataDto}
	*/
	Optional<StateData> findDtoBySurveyUnitId(String id);
	/**
	* This method retrieve the Data for a specific reporting_unit
	* 
	* @param id the id of reporting unit
	* @return {@link Data}
	*/
	Optional<StateData> findBySurveyUnitId(String id);
}
