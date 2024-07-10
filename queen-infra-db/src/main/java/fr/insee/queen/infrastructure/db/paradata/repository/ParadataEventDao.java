package fr.insee.queen.infrastructure.db.paradata.repository;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.paradata.gateway.ParadataEventRepository;
import fr.insee.queen.infrastructure.db.paradata.repository.jpa.ParadataEventJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class ParadataEventDao implements ParadataEventRepository {
    private final ParadataEventJpaRepository jpaRepository;

    @Override
    public void createParadataEvent(UUID id, ObjectNode paradataValue, String surveyUnitId) {
        jpaRepository.createParadataEvent(id, paradataValue, surveyUnitId);
    }
}
