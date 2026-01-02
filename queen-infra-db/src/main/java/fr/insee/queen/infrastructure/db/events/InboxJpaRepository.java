package fr.insee.queen.infrastructure.db.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InboxJpaRepository extends JpaRepository<InboxDB, UUID> {

    @Query("SELECT i FROM InboxDB i ORDER BY i.createdDate ASC")
    List<InboxDB> findAllOrderByCreatedDate();
}