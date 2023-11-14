package fr.insee.queen.api.paradata.repository.jpa;

import fr.insee.queen.api.paradata.repository.entity.ParadataEventDB;
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
     * Create paradata for a survey unit
     *
     * @param id paradata id
     * @param paradataValue paradata value (json format)
     * @param surveyUnitId survey unit id
     */
    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO paradata_event (id, value, survey_unit_id)
            VALUES (:id, :paradataValue\\:\\:jsonb, :surveyUnitId)""", nativeQuery = true)
    void createParadataEvent(UUID id, String paradataValue, String surveyUnitId);

    /**
     * Delete all survey unit's paradatas for a campaign
     * @param campaignId campaign id
     */
    @Transactional
    @Modifying
    @Query(value = """
            delete from paradata_event p where id in (
                select p.id from survey_unit s
                    where text(s.id) = p.value->>'idSU'
                    and s.campaign_id = :campaignId
            )""", nativeQuery = true)
    void deleteParadataEvents(String campaignId);
}
