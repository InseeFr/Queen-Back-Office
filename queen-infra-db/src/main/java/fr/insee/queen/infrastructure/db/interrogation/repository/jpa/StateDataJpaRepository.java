package fr.insee.queen.infrastructure.db.interrogation.repository.jpa;

import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.infrastructure.db.interrogation.entity.StateDataDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository to handle interrogation's state data
 */
@Repository
public interface StateDataJpaRepository extends JpaRepository<StateDataDB, UUID> {
    /**
     * Find state data for an interrogation
     *
     * @param interrogationId interrogation id
     * @return {@link Optional<StateData>} state data of the interrogation
     */
    Optional<StateData> findByInterrogationId(String interrogationId);

    /**
     * Update state data of an interrogation
     * @param interrogationId interrogation to update
     * @param date state date
     * @param currentPage state current page
     * @param state state type
     * @return number of rows updated
     */
    @Transactional
    @Modifying
    @Query("UPDATE StateDataDB s SET s.currentPage=:currentPage, s.date=:date, s.state=:state WHERE s.interrogation.id=:interrogationId")
    int updateStateData(String interrogationId, Long date, String currentPage, StateDataType state);

    /**
     * Delete all interrogations state data linked to a campaign
     *
     * @param campaignId campaign id
     */
    @Transactional
    @Modifying
    @Query(value = """
            delete from state_data where interrogation_id in (
                select id from interrogation
                    where campaign_id = :campaignId
            )""", nativeQuery = true)
    void deleteStateDatas(String campaignId);

    /**
     * Check if a state data exists for an interrogation
     * @param interrogationId interrogation to check
     * @return true if state data exists, false otherwise
     */
    boolean existsByInterrogationId(String interrogationId);

    /**
     * Delete state data by interrogation
     * @param interrogationId interrogation id
     */
    @Transactional
    @Modifying
    void deleteByInterrogationId(String interrogationId);
}
