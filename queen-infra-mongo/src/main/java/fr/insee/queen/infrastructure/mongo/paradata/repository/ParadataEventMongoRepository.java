package fr.insee.queen.infrastructure.mongo.paradata.repository;

import fr.insee.queen.infrastructure.mongo.paradata.document.ParadataEventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Mongo repository to handle paradata
 */
@Repository
public interface ParadataEventMongoRepository extends MongoRepository<ParadataEventDocument, UUID> {
    /**
     * Delete paradatas linked to survey unit
     * @param surveyUnitId survey unit id
     */
    void deleteBySurveyUnitId(String surveyUnitId);
}
