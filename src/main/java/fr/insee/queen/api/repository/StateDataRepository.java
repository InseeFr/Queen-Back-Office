package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.domain.StateDataType;
import fr.insee.queen.api.dto.statedata.StateDataDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
* StateDataRepository is the repository using to access to  StateData table in DB
* 
* @author Claudel Benjamin
* 
*/
@Repository
public interface StateDataRepository extends JpaRepository<StateData, UUID> {
	/**
	 * This method retrieve the StateData for a specific reporting_unit
	 *
	 * @param surveyUnitId the id of reporting unit
	 * @return {@link StateDataDto}
	 */
	Optional<StateDataDto> findBySurveyUnitId(String surveyUnitId);

	/**
	 * This method retrieve the StateData for a specific reporting_unit
	 *
	 * @param surveyUnitId the id of reporting unit
	 * @return {@link StateData}
	 */
	Optional<StateData> findEntityBySurveyUnitId(String surveyUnitId);

	@Modifying
	@Query("UPDATE StateData s SET s.currentPage=:currentPage, s.date=:date, s.state=:state WHERE s.surveyUnit.id=:surveyUnitId")
	void updateStateData(String surveyUnitId, Long date, String currentPage, StateDataType state);

	@Modifying
	@Query(value = """
	delete from state_data st where id in (
	    select st.id from survey_unit s
	        where s.id = st.survey_unit_id
	        and s.campaign_id = :campaignId
	)""", nativeQuery = true)
	void deleteStateDatas(String campaignId);
}
