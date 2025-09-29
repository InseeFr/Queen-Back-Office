package fr.insee.queen.infrastructure.db.interrogationtempzone.repository;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogationtempzone.gateway.InterrogationTempZoneRepository;
import fr.insee.queen.domain.interrogationtempzone.model.InterrogationTempZone;
import fr.insee.queen.infrastructure.db.interrogationtempzone.repository.jpa.InterrogationTempZoneJpaRepository;
import fr.insee.queen.infrastructure.db.interrogationtempzone.entity.InterrogationTempZoneDB;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to handle interrogations in temp zone.
 * A interrogation is going to the temporary zone when an interviewer puts the interrogation while this interrogation is
 * not affected to the interviewer
 * In this case, problems are solved later ... (or not)
 *
 * @author Laurent Caouissin
 */
@Repository
@AllArgsConstructor
public class InterrogationTempZoneDao implements InterrogationTempZoneRepository {
    private final InterrogationTempZoneJpaRepository jpaRepository;

    @Override
    public List<InterrogationTempZone> getAllInterrogations() {
        return jpaRepository.findAllProjectedBy();
    }

    @Override
    public void save(String interrogationId, String userId, Long date, ObjectNode interrogation) {
        InterrogationTempZoneDB interrogationTempZone = new InterrogationTempZoneDB(interrogationId, userId, date, interrogation);
        jpaRepository.save(interrogationTempZone);
    }
}
