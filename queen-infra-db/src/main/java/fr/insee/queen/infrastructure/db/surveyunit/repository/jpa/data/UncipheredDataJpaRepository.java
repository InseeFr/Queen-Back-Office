package fr.insee.queen.infrastructure.db.surveyunit.repository.jpa.data;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.surveyunit.entity.DataDB;
import fr.insee.queen.infrastructure.db.surveyunit.repository.jpa.DataJpaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = "feature.cipher.enabled", havingValue = "true")
@Repository
public interface UncipheredDataJpaRepository extends JpaRepository<DataDB, UUID>, DataJpaRepository {
    /**
     * Delete all survey units data for a campaign
     *
     * @param campaignId campaign id
     */
    @Transactional
    @Modifying
    @Query(value = DataQueryConstants.DELETE_QUERY, nativeQuery = true)
    void deleteDatas(String campaignId);

    /**
     * Find the data of a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return an optional of the data (json format)
     */
    @Query(value = DataQueryConstants.FIND_QUERY)
    Optional<ObjectNode> findData(String surveyUnitId);

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
     * Delete data of a survey unit
     * @param surveyUnitId survey unit id
     */
    @Transactional
    @Modifying
    void deleteBySurveyUnitId(String surveyUnitId);

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
}
