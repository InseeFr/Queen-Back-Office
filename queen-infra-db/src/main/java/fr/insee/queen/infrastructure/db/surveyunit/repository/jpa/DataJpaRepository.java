package fr.insee.queen.infrastructure.db.surveyunit.repository.jpa;

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
    int updateData(String surveyUnitId, String data);

    /**
     * Update data for a survey unit
     *
     * @param surveyUnitId survey unit id
     * @param collectedUpdateData partial collected data to set on current collected data
     * @return number of updated rows
     */
    @Transactional
    @Modifying
    @Query(value = """
        update data
        set value=jsonb_set(cast(value as jsonb), '{COLLECTED}', aggregate_query.updated_collected_data, true)
        from(
            select
                case
                    when(value->>'COLLECTED') IS NULL then cast(:collectedUpdateData as jsonb)
                    else (value->'COLLECTED' || cast(:collectedUpdateData as jsonb))
                end as updated_collected_data
            from data
            where survey_unit_id=:surveyUnitId
        ) as aggregate_query
        where survey_unit_id=:surveyUnitId
    """, nativeQuery = true)
    void updateCollectedData(String surveyUnitId, String collectedUpdateData);

    /**
     * Find the data of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return an optional of the data (json format)
     */
    @Query("select s.data.value from SurveyUnitDB s where s.id=:surveyUnitId")
    Optional<String> findData(String surveyUnitId);

    /**
     * Find the data of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return an optional of the data (json format)
     */
    @Query("select s.data.value from SurveyUnitDB s where s.id=:surveyUnitId")
    String getData(String surveyUnitId);

    /**
     * Delete data of a survey unit
     * @param surveyUnitId survey unit id
     */
    @Transactional
    @Modifying
    void deleteBySurveyUnitId(String surveyUnitId);
}
