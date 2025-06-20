package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.paradata.entity.ParadataEventDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventsJpaRepository extends JpaRepository<EventsDB, UUID>  {

    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO events (id, value)
            VALUES (:id, :event\\:\\:jsonb)""", nativeQuery = true)
    void createEvent(UUID id, ObjectNode event);


    List<EventsDB> findByUpdatedDateIsNull();

    @Modifying
    @Transactional
    @Query("UPDATE EventsDB e SET e.updatedDate = CURRENT_TIMESTAMP WHERE e.id = :id")
    void setUpdatedDateToNow(@Param("id") UUID id);
}
