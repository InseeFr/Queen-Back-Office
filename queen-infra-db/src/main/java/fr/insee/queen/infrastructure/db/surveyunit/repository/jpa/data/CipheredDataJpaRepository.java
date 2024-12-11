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

@ConditionalOnProperty(name = "feature.cipher.enabled", havingValue = "true")
@Repository
public interface CipheredDataJpaRepository extends JpaRepository<DataDB, UUID>, DataJpaRepository {

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
    @Query(nativeQuery = true,
        value = """
            update data
                set value = pgp_sym_encrypt(:data\\:\\:text, current_setting('data.encryption.key'))
                where survey_unit_id = :surveyUnitId"""
    )
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
        WITH decrypted_data AS (
            SELECT
                id,
                pgp_sym_decrypt(value, current_setting('data.encryption.key'))::jsonb AS decrypted_value
            FROM
                data
            WHERE
                survey_unit_id = :surveyUnitId
        )
        UPDATE data
        SET value = pgp_sym_encrypt(
                        jsonb_set(
                            COALESCE(decrypted_data.decrypted_value, '{}'),
                            '{COLLECTED}',
                            COALESCE(decrypted_data.decrypted_value->'COLLECTED', '{}'::jsonb) || :collectedUpdateData
                        )::text,
                        current_setting('data.encryption.key')
                    )
        FROM decrypted_data
        WHERE data.id = decrypted_data.id
          AND data.survey_unit_id = :surveyUnitId;
    """, nativeQuery = true)
    void updateCollectedData(String surveyUnitId, ObjectNode collectedUpdateData);
}
