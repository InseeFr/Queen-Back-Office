package fr.insee.queen.api.paradata.repository;

import fr.insee.queen.api.paradata.repository.jpa.ParadataEventJpaRepository;
import fr.insee.queen.api.paradata.service.gateway.ParadataEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class ParadataEventDao implements ParadataEventRepository {
    private final ParadataEventJpaRepository jpaRepository;

    @Override
    public void createParadataEvent(UUID id, String paradataValue, String surveyUnitId) {
        jpaRepository.createParadataEvent(id, paradataValue, surveyUnitId);
    }

    @Override
    public void deleteParadataEvents(String campaignId) {
        jpaRepository.deleteParadataEvents(campaignId);
    }
}
