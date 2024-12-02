package fr.insee.queen.infrastructure.mongo.paradata.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.paradata.gateway.ParadataEventRepository;
import fr.insee.queen.infrastructure.mongo.paradata.document.ParadataEventData;
import fr.insee.queen.infrastructure.mongo.paradata.document.ParadataEventDocument;
import fr.insee.queen.infrastructure.mongo.paradata.repository.ParadataEventMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ParadataEventMongoDao implements ParadataEventRepository {
    private final ParadataEventMongoRepository repository;

    @Override
    public void createParadataEvent(UUID id, ObjectNode paradataValue, String surveyUnitId) {
        ParadataEventData data = new ParadataEventData(paradataValue);
        ParadataEventDocument paradata = new ParadataEventDocument(id, data, surveyUnitId);
        repository.save(paradata);
    }
}
