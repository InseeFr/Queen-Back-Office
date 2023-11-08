package fr.insee.queen.api.service.surveyunit;

import fr.insee.queen.api.repository.jpa.ParadataEventJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ParadataEventApiService implements ParadataEventService {

    private final ParadataEventJpaRepository paradataEventRepository;

    @Override
    public void createParadataEvent(String surveyUnitId, String paradataValue) {
        paradataEventRepository.createParadataEvent(UUID.randomUUID(), paradataValue, surveyUnitId);
    }
}
