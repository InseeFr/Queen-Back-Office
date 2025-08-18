package fr.insee.queen.infrastructure.db.interrogationtempzone.repository.jpa;

import fr.insee.queen.domain.interrogationtempzone.model.InterrogationTempZone;
import fr.insee.queen.infrastructure.db.interrogationtempzone.entity.InterrogationTempZoneDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * JPA repository to handle interrogations in temp zone
 *
 * @author Laurent Caouissin
 */
@Repository
public interface InterrogationTempZoneJpaRepository extends JpaRepository<InterrogationTempZoneDB, String> {

    void deleteByInterrogationId(String id);

    List<InterrogationTempZone> findAllProjectedBy();

    @Transactional
    @Modifying
    @Query(value = """
            delete from interrogation_temp_zone where interrogation_id in (
                select id from interrogation
                    where campaign_id = :campaignId
            )""", nativeQuery = true)
    void deleteInterrogations(String campaignId);
}
