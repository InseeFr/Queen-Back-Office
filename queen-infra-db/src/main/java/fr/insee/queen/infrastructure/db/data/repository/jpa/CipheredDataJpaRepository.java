package fr.insee.queen.infrastructure.db.data.repository.jpa;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@ConditionalOnProperty(name = "feature.sensitive-data.enabled", havingValue = "true")
@Repository
public interface CipheredDataJpaRepository extends DataJpaRepository {

    /**
     * Update data for an interrogation
     *
     * @param interrogationId interrogation id
     * @param data json data to set
     * @return number of updated rows
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true,
        value = """
            update data
                set value = pgp_sym_encrypt(:data\\:\\:text, current_setting('data.encryption.key'), 's2k-count=65536')
                where interrogation_id = :interrogationId"""
    )
    int updateData(String interrogationId, ObjectNode data);

    /**
     * Update data for an interrogation
     *
     * @param interrogationId interrogation id
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
                interrogation_id = :interrogationId
        )
        UPDATE data
        SET value = pgp_sym_encrypt(
                        jsonb_set(
                            COALESCE(decrypted_data.decrypted_value, '{}'),
                            '{COLLECTED}',
                            COALESCE(decrypted_data.decrypted_value->'COLLECTED', '{}'::jsonb) || :collectedUpdateData
                        )::text,
                        current_setting('data.encryption.key'),
                        's2k-count=65536'
                    )
        FROM decrypted_data
        WHERE data.id = decrypted_data.id
          AND data.interrogation_id = :interrogationId;
    """, nativeQuery = true)
    void updateCollectedData(String interrogationId, ObjectNode collectedUpdateData);

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE data
            SET value = pgp_sym_encrypt('{}', current_setting('data.encryption.key'), 's2k-count=65536')
            WHERE interrogation_id IN (
                SELECT su.id
                FROM interrogation su
                INNER JOIN state_data sd ON sd.interrogation_id = su.id
                WHERE su.campaign_id = :campaignId AND sd.state = 'EXTRACTED'
                AND sd.date BETWEEN :startTimestamp AND :endTimestamp
            );
    """, nativeQuery = true)
    void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp);
}
