package fr.insee.queen.infrastructure.db.surveyunit.repository.jpa;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.surveyunit.entity.DataDB;
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
            delete from data where survey_unit_id in (
                select id from survey_unit
                    where campaign_id = :campaignId
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
    int updateData(String surveyUnitId, ObjectNode data);

    /**
     * Update data for a survey unit
     *
     * @param surveyUnitId survey unit id
     * @param collectedUpdateData partial collected data to set on current collected data
     */
    @Transactional
    @Modifying
    @Query(value = """
        update data
            set value = jsonb_set(coalesce(value, '{}'),
                        '{COLLECTED}',
                        coalesce(value->'COLLECTED', '{}') || :collectedUpdateData)
            where survey_unit_id=:surveyUnitId
    """, nativeQuery = true)
    void updateCollectedData(String surveyUnitId, ObjectNode collectedUpdateData);

    /**
     * Find the data of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return an optional of the data (json format)
     */
    @Query("select s.data.value from SurveyUnitDB s where s.id=:surveyUnitId")
    Optional<ObjectNode> findData(String surveyUnitId);

    /**
     * Find the data of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return an optional of the data (json format)
     */
    @Query("select s.data.value from SurveyUnitDB s where s.id=:surveyUnitId")
    ObjectNode getData(String surveyUnitId);

    /**
     * Delete data of a survey unit
     * @param surveyUnitId survey unit id
     */
    @Transactional
    @Modifying
    void deleteBySurveyUnitId(String surveyUnitId);
}
