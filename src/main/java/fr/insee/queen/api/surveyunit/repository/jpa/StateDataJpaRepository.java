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
 * StateDataRepository is the repository using to access to  StateData table in DB
 *
 * @author Claudel Benjamin
 */
@Repository
public interface StateDataJpaRepository extends JpaRepository<StateDataDB, UUID> {
    /**
     * This method retrieve the StateData for a specific reporting_unit
     *
     * @param surveyUnitId the id of reporting unit
     * @return {@link StateData}
     */
    Optional<StateData> findBySurveyUnitId(String surveyUnitId);

    @Transactional
    @Modifying
    @Query("UPDATE StateDataDB s SET s.currentPage=:currentPage, s.date=:date, s.state=:state WHERE s.surveyUnit.id=:surveyUnitId")
    int updateStateData(String surveyUnitId, Long date, String currentPage, StateDataType state);

    @Transactional
    @Modifying
    @Query(value = """
            delete from state_data st where id in (
                select st.id from survey_unit s
                    where s.id = st.survey_unit_id
                    and s.campaign_id = :campaignId
            )""", nativeQuery = true)
    void deleteStateDatas(String campaignId);

    boolean existsBySurveyUnitId(String surveyUnitId);

    void deleteBySurveyUnitId(String id);
}
