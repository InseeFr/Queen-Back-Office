package fr.insee.queen.infrastructure.db.data.repository.jpa;

import tools.jackson.databind.node.ObjectNode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA repository to handle interrogation's data response for a questionnaire
 */
@ConditionalOnProperty(name = "feature.sensitive-data.enabled", havingValue = "false")
@Repository
public interface UncipheredDataJpaRepository extends DataJpaRepository {

    /**
     * Update data for an interrogation
     *
     * @param interrogationId interrogation id
     * @param collectedUpdateData partial collected data to set on current collected data
     */
    @Transactional
    @Modifying
    @NativeQuery("""
        update data
            set value = jsonb_set(coalesce(value, '{}'),
                        '{COLLECTED}',
                        coalesce(value->'COLLECTED', '{}') || :collectedUpdateData)
            where interrogation_id=:interrogationId
    """)
    void updateCollectedData(String interrogationId, ObjectNode collectedUpdateData);

}
