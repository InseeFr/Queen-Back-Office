package fr.insee.queen.infrastructure.mongo.surveyunittempzone.repository;

import fr.insee.queen.infrastructure.mongo.surveyunittempzone.document.SurveyUnitTempZoneDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository to handle survey units in temp zone
 *
 * @author Laurent Caouissin
 */
@Repository
public interface SurveyUnitTempZoneMongoRepository extends MongoRepository<SurveyUnitTempZoneDocument, String> {

    void deleteBySurveyUnitId(String id);
}
