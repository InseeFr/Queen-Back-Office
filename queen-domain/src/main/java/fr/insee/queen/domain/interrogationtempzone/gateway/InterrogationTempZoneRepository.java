package fr.insee.queen.domain.interrogationtempzone.gateway;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogationtempzone.model.InterrogationTempZone;

import java.util.List;

/**
 * Repository to handle interrogations in temp zone
 *
 * @author Laurent Caouissin
 */
public interface InterrogationTempZoneRepository {

    void delete(String interrogationId);

    List<InterrogationTempZone> getAllInterrogations();

    void save(String interrogationId, String userId, Long date, ObjectNode interrogation);
}
