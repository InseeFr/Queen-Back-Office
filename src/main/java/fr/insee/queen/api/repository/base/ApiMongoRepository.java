package fr.insee.queen.api.repository.base;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import fr.insee.queen.api.repository.ApiRepository;

public class ApiMongoRepository<T, I> extends SimpleMongoRepository<T, I> implements ApiRepository<T, I> {
    public ApiMongoRepository(MongoEntityInformation<T, I> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
    }
}