package fr.insee.queen.infrastructure.db.data.repository.jpa;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA repository to handle survey unit's data response for a questionnaire
 */
@ConditionalOnProperty(name = "feature.sensitive-data.enabled", havingValue = "false")
@Repository
public interface UncipheredDataJpaRepository extends DataJpaRepository {

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

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE data
            SET value = '{}'::jsonb
            WHERE survey_unit_id IN (
                SELECT su.id
                FROM survey_unit su
                INNER JOIN state_data sd ON sd.survey_unit_id = su.id
                WHERE su.campaign_id = :campaignId AND sd.state = 'EXTRACTED'
                AND sd.date BETWEEN :startTimestamp AND :endTimestamp
            );
    """, nativeQuery = true)
    void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp);
}
