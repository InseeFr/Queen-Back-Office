package fr.insee.queen.infrastructure.db.interrogation.repository.jpa;

import fr.insee.queen.infrastructure.db.interrogation.entity.LeafStateDB;
import fr.insee.queen.infrastructure.db.interrogation.projection.LeafStateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeafStateJpaRepository extends JpaRepository<LeafStateDB, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM LeafStateDB l WHERE l.stateData.id = :stateDataId")
    void deleteByStateDataId(UUID stateDataId);

    @Query("""
        select new fr.insee.queen.infrastructure.db.interrogation.projection.LeafStateProjection(
            i.id,
            l.state,
            l.date
        )
        from LeafStateDB l
        join l.stateData sd
        join sd.interrogation i
        where i.id = :interrogationId""")
    List<LeafStateProjection> findByInterrogationId(String interrogationId);

    /**
     * Find leaf states linked to several interrogations (batch loading, avoids N+1)
     *
     * @param interrogationIds interrogation ids
     * @return List of {@link LeafStateProjection} leaf states
     */
    @Query("""
            select new fr.insee.queen.infrastructure.db.interrogation.projection.LeafStateProjection(
                i.id,
                l.state,
                l.date
            )
            from LeafStateDB l
            join l.stateData sd
            join sd.interrogation i
            where i.id in :interrogationIds""")
    List<LeafStateProjection> findByInterrogationIdIn(List<String> interrogationIds);
}
