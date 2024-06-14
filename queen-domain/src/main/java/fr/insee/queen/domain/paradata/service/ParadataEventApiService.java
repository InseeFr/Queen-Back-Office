package fr.insee.queen.domain.paradata.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.paradata.gateway.ParadataEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ParadataEventApiService implements ParadataEventService {

    private final ParadataEventRepository paradataEventRepository;

    @Override
    public void createParadataEvent(String surveyUnitId, ObjectNode paradataValue) {
        paradataEventRepository.createParadataEvent(UUID.randomUUID(), paradataValue, surveyUnitId);
    }
}
