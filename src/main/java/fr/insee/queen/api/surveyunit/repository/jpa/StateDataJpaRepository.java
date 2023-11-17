package fr.insee.queen.api.surveyunit.repository.jpa;

import fr.insee.queen.api.depositproof.service.model.StateDataType;
import fr.insee.queen.api.surveyunit.repository.entity.StateDataDB;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository to handle survey unit's state data
 */
@Repository
public interface StateDataJpaRepository extends JpaRepository<StateDataDB, UUID> {
    /**
     * Find state data for a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return {@link Optional<StateData>} state data of the survey unit
     */
    Optional<StateData> findBySurveyUnitId(String surveyUnitId);

    /**
     * Update state data of a survey unit
     * @param surveyUnitId survey unit to update
     * @param date state date
     * @param currentPage state current page
     * @param state state type
     * @return number of rows updated
     */
    @Transactional
    @Modifying
    @Query("UPDATE StateDataDB s SET s.currentPage=:currentPage, s.date=:date, s.state=:state WHERE s.surveyUnit.id=:surveyUnitId")
    int updateStateData(String surveyUnitId, Long date, String currentPage, StateDataType state);

    /**
     * Delete all survey units state data linked to a campaign
     *
     * @param campaignId campaign id
     */
    @Transactional
    @Modifying
    @Query(value = """
            delete from state_data where survey_unit_id in (
                select id from survey_unit
                    where campaign_id = :campaignId
            )""", nativeQuery = true)
    void deleteStateDatas(String campaignId);

    /**
     * Check if a state data exists for a survey unit
     * @param surveyUnitId survey unit to check
     * @return true if state data exists, false otherwise
     */
    boolean existsBySurveyUnitId(String surveyUnitId);

    /**
     * Delete state data by survey unit
     * @param surveyUnitId survey unit id
     */
    @Transactional
    @Modifying
    void deleteBySurveyUnitId(String surveyUnitId);
}
