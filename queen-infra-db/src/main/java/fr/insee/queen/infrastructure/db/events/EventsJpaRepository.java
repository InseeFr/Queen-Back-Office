package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Repository
public interface EventsJpaRepository extends JpaRepository<OutboxDB, UUID>  {

    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO outbox (id, payload)
            VALUES (:id, :event\\:\\:jsonb)""", nativeQuery = true)
    void createEvent(UUID id, ObjectNode event);

}