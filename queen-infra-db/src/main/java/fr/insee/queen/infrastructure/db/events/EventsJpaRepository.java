package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventsJpaRepository extends JpaRepository<OutboxDB, UUID>  {

    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO outbox (id, payload)
            VALUES (:id, :event\\:\\:jsonb)""", nativeQuery = true)
    void createEvent(UUID id, ObjectNode event);

    @Query("SELECT o FROM OutboxDB o WHERE o.processedDate IS NULL ORDER BY o.createdDate ASC")
    List<OutboxDB> findUnprocessedEvents();

    @Transactional
    @Modifying
    @Query("UPDATE OutboxDB o SET o.processedDate = :processedDate WHERE o.id = :id")
    void markAsProcessed(@Param("id") UUID id, @Param("processedDate") LocalDateTime processedDate);

}