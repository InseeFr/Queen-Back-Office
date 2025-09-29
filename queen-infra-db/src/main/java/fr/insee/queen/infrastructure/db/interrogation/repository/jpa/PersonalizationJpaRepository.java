package fr.insee.queen.infrastructure.db.interrogation.repository.jpa;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.infrastructure.db.interrogation.entity.PersonalizationDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository to handle interrogations personalization data
 */
@Repository
public interface PersonalizationJpaRepository extends JpaRepository<PersonalizationDB, UUID> {

    /**
     * Update personalization for an interrogation
     *
     * @param interrogationId interrogation id
     * @param personalization json personalization to set
     * @return number of updated rows
     */
    @Transactional
    @Modifying
    @Query("update PersonalizationDB p set p.value = :personalization where p.interrogation.id = :interrogationId")
    int updatePersonalization(String interrogationId, ArrayNode personalization);

    /**
     * Find the personalization of an interrogation
     *
     * @param interrogationId interrogation id
     * @return an optional of the personalization (json format)
     */
    @Query("select s.personalization.value from InterrogationDB s where s.id=:interrogationId")
    Optional<ArrayNode> findPersonalization(String interrogationId);
}
