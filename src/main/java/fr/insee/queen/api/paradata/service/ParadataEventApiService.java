package fr.insee.queen.api.paradata.service;

import fr.insee.queen.api.paradata.service.gateway.ParadataEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ParadataEventApiService implements ParadataEventService {

    private final ParadataEventRepository paradataEventRepository;

    @Override
    public void createParadataEvent(String surveyUnitId, String paradataValue) {
        paradataEventRepository.createParadataEvent(UUID.randomUUID(), paradataValue, surveyUnitId);
    }
}
