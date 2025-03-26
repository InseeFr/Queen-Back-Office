package fr.insee.queen.infrastructure.db.data.repository.jpa;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.data.entity.common.DataDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface used to centralize common sql queries between cipher/non cipher data jpa repositories
 */
@NoRepositoryBean
public interface DataJpaRepository extends JpaRepository<DataDB, UUID>, DataRepository {
    /**
     * Delete all interrogations data for a campaign
     *
     * @param campaignId campaign id
     */
    @Transactional
    @Modifying
    @Query(value = """
            delete from data where interrogation_id in (
                select id from interrogation
                    where campaign_id = :campaignId
            )""", nativeQuery = true)
    void deleteDatas(String campaignId);

    /**
     * Find the data of a interrogation
     *
     * @param interrogationId interrogation id
     * @return an optional of the data (json format)
     */
    @Query(value = """
            select s.data.value from InterrogationDB s
            where s.id=:interrogationId
       """)
    Optional<ObjectNode> findData(String interrogationId);

    /**
     * Delete data of a interrogation
     * @param interrogationId interrogation id
     */
    @Transactional
    @Modifying
    @Query(value =
        """
            delete from data where interrogation_id = :interrogationId
        """, nativeQuery = true)
    void deleteByInterrogationId(String interrogationId);

    /**
     * Update data for a interrogation
     *
     * @param interrogationId interrogation id
     * @param data json data to set
     * @return number of updated rows
     */
    @Transactional
    @Modifying
    @Query("update DataDB d set d.value = :data where d.interrogation.id = :interrogationId")
    int updateData(String interrogationId, ObjectNode data);
}
