package fr.insee.queen.domain.interrogationtempzone.service;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogationtempzone.gateway.InterrogationTempZoneRepository;
import fr.insee.queen.domain.interrogationtempzone.model.InterrogationTempZone;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class InterrogationTempZoneApiService implements InterrogationTempZoneService {
    private final InterrogationTempZoneRepository interrogationTempZoneRepository;
    private final Clock clock;

    @Override
    public void saveInterrogationToTempZone(String interrogationId, String userId, ObjectNode interrogationData) {
        long date = clock.instant().toEpochMilli();
        interrogationTempZoneRepository.save(interrogationId, userId, date, interrogationData);
    }

    @Override
    public List<InterrogationTempZone> getAllInterrogationTempZone() {
        return interrogationTempZoneRepository.getAllInterrogations();
    }
}
