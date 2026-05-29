package fr.insee.queen.infrastructure.db.data.repository.jpa;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.data.entity.common.DataDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface used to centralize common sql queries between cipher/non cipher data jpa repositories
 */
@NoRepositoryBean
public interface DataJpaRepository extends JpaRepository<DataDB, UUID>, DataRepository {
    /**
     * Find the data of an interrogation
     *
     * @param interrogationId interrogation id
     * @return an optional of the data (json format)
     */
    @Query(value = """
            select s.data.value from InterrogationDB s
            where s.id=:interrogationId
       """)
    Optional<ObjectNode> findData(String interrogationId);

    /**
     * Update data for an interrogation
     *
     * @param interrogationId interrogation id
     * @param data json data to set
     * @return number of updated rows
     */
    @Transactional
    @Modifying
    @Query("update DataDB d set d.value = :data where d.interrogation.id = :interrogationId")
    int updateData(String interrogationId, ObjectNode data);

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE data
            SET value = '{}'::jsonb
            WHERE interrogation_id IN (
                SELECT su.id
                FROM interrogation su
                INNER JOIN state_data sd ON sd.interrogation_id = su.id
                WHERE su.campaign_id = :campaignId AND sd.state = 'EXTRACTED'
                AND sd.date BETWEEN :startTimestamp AND :endTimestamp
            );
    """, nativeQuery = true)
    void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp);

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE data
            SET value = '{}'::jsonb
            WHERE interrogation_id IN (
                SELECT su.id
                FROM interrogation su
                INNER JOIN state_data sd ON sd.interrogation_id = su.id
                WHERE su.campaign_id = :campaignId AND sd.state = 'EXTRACTED'
                AND su.id IN (:interrogationIds)
            );
    """, nativeQuery = true)
    void cleanExtractedDataByIds(String campaignId, List<String> interrogationIds);
}
