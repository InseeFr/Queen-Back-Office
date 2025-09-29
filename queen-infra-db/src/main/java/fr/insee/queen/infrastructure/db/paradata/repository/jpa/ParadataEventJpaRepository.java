package fr.insee.queen.infrastructure.db.paradata.repository.jpa;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.paradata.entity.ParadataEventDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * JPA repository to handle paradata in DB
 */
@Repository
public interface ParadataEventJpaRepository extends JpaRepository<ParadataEventDB, UUID> {

    /**
     * Create paradata for an interrogation
     *
     * @param id paradata id
     * @param paradataValue paradata value (json format)
     * @param interrogationId interrogation id
     */
    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO paradata_event (id, value, interrogation_id, survey_unit_id)
            VALUES (:id, :paradataValue\\:\\:jsonb, :interrogationId, :surveyUnitId)""", nativeQuery = true)
    void createParadataEvent(UUID id, ObjectNode paradataValue, String interrogationId, String surveyUnitId);
}
