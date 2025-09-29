package fr.insee.queen.infrastructure.db.interrogationtempzone.repository.jpa;

import fr.insee.queen.domain.interrogationtempzone.model.InterrogationTempZone;
import fr.insee.queen.infrastructure.db.interrogationtempzone.entity.InterrogationTempZoneDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository to handle interrogations in temp zone
 *
 * @author Laurent Caouissin
 */
@Repository
public interface InterrogationTempZoneJpaRepository extends JpaRepository<InterrogationTempZoneDB, String> {

    List<InterrogationTempZone> findAllProjectedBy();
}
