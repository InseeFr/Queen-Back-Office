package fr.insee.queen.infrastructure.mongo.questionnaire.repository;

import fr.insee.queen.infrastructure.mongo.questionnaire.document.NomenclatureDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Mongo repository to handle nomenclatures
 */
@Repository
public interface NomenclatureMongoRepository extends MongoRepository<NomenclatureDocument, String> {

    @Query(value = "{ }", fields = "{ '_id' : 1 }", sort = "{ '_id' : 1 }")
    Optional<List<NomenclatureDocument>> findAllNomenclatureIds();
}
