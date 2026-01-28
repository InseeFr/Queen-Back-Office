package fr.insee.queen.infrastructure.db.interrogation.repository.jpa;

import fr.insee.queen.infrastructure.db.interrogation.entity.LeafStateDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface LeafStateJpaRepository extends JpaRepository<LeafStateDB, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM LeafStateDB l WHERE l.stateData.id = :stateDataId")
    void deleteByStateDataId(UUID stateDataId);
}
