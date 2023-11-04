package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.dto.statedata.StateDataType;
import fr.insee.queen.api.entity.StateDataDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * StateDataRepository is the repository using to access to  StateData table in DB
 *
 * @author Claudel Benjamin
 *
 */
@Repository
public interface StateDataCrudRepository extends JpaRepository<StateDataDB, UUID> {
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
     * @return {@link StateDataDB}
     */
    Optional<StateDataDB> findEntityBySurveyUnitId(String surveyUnitId);

    StateDataDB getBySurveyUnitId(String surveyUnitId);

    @Transactional
    @Modifying
    @Query("UPDATE StateDataDB s SET s.currentPage=:currentPage, s.date=:date, s.state=:state WHERE s.surveyUnit.id=:surveyUnitId")
    void updateStateData(String surveyUnitId, Long date, String currentPage, StateDataType state);

    @Transactional
    @Modifying
    @Query(value = """
	delete from state_data st where id in (
	    select st.id from survey_unit s
	        where s.id = st.survey_unit_id
	        and s.campaign_id = :campaignId
	)""", nativeQuery = true)
    void deleteStateDatas(String campaignId);

    @Transactional
    @Modifying
    @Query(value = """
		INSERT into state_data(id) 
		values (:id)""", nativeQuery = true)
    void createStateData(UUID id);

    boolean existsBySurveyUnitId(String surveyUnitId);
}
