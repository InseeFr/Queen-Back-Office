package fr.insee.queen.domain.interrogationtempzone.service;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogationtempzone.model.InterrogationTempZone;

import java.util.List;

public interface InterrogationTempZoneService {
    void saveInterrogationToTempZone(String interrogationId, String userId, ObjectNode interrogationData);

    List<InterrogationTempZone> getAllInterrogationTempZone();
}
