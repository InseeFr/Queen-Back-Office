package fr.insee.queen.api.surveyunit.repository.jpa;

import fr.insee.queen.api.surveyunit.repository.entity.DataDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository to handle survey unit's data response for a questionnaire
 */
@Repository
public interface DataJpaRepository extends JpaRepository<DataDB, UUID> {
    /**
     * Delete all survey units data for a campaign
     *
     * @param campaignId campaign id
     */
    @Transactional
    @Modifying
    @Query(value = """
            delete from data d where id in (
                select d.id from survey_unit s
                    where s.id = d.survey_unit_id
                    and s.campaign_id = :campaignId
            )""", nativeQuery = true)
    void deleteDatas(String campaignId);

    /**
     * Update data for a survey unit
     *
     * @param surveyUnitId survey unit id
     * @param data json data to set
     * @return number of updated rows
     */
    @Transactional
    @Modifying
    @Query("update DataDB d set d.value = :data where d.surveyUnit.id = :surveyUnitId")
    int updateData(String surveyUnitId, String data);

    /**
     * Find the data of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return an optional of the data (json format)
     */
    @Query("select s.data.value from SurveyUnitDB s where s.id=:surveyUnitId")
    Optional<String> findData(String surveyUnitId);

    /**
     * Delete data of a survey unit
     * @param surveyUnitId survey unit id
     */
    void deleteBySurveyUnitId(String surveyUnitId);
}
