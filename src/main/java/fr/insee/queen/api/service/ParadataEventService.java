package fr.insee.queen.api.service;

import fr.insee.queen.api.repository.ParadataEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class ParadataEventService {

    private final ParadataEventRepository paradataEventRepository;

    public void createParadataEvent(String surveyUnitId, String paradataValue) {
        paradataEventRepository.createParadataEvent(UUID.randomUUID(), paradataValue, surveyUnitId);
    }
}
