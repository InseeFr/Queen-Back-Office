package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.dto.statedata.StateDataDto;
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
@Repository
public interface StateDataRepository extends JpaRepository<StateData, UUID> {
	/**
	* This method retrieve the Data for a specific reporting_unit
	* 
	* @param surveyUnitId the id of reporting unit
	* @return {@link StateData}
	*/
	Optional<StateDataDto> findBySurveyUnitId(String surveyUnitId);
}
