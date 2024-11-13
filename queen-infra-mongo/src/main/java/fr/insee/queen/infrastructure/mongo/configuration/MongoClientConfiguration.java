package fr.insee.queen.infrastructure.mongo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.infrastructure.mongo.converter.ArrayNodeToDocumentConverter;
import fr.insee.queen.infrastructure.mongo.converter.DocumentToArrayNodeConverter;
import fr.insee.queen.infrastructure.mongo.converter.DocumentToObjectNodeConverter;
import fr.insee.queen.infrastructure.mongo.converter.ObjectNodeToDocumentConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
@ConditionalOnProperty(name = "feature.mongo.enabled", havingValue = "true")
@RequiredArgsConstructor
public class MongoClientConfiguration extends AbstractMongoClientConfiguration {
    private final ObjectMapper mapper;

    @Value("${spring.data.mongodb.database}")
    private final String databaseName;

    @Override
    protected @NonNull String getDatabaseName() {
        return databaseName;
    }

    @Override
    protected void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter adapter) {
        adapter.registerConverter(new DocumentToObjectNodeConverter(mapper));
        adapter.registerConverter(new ObjectNodeToDocumentConverter());
        adapter.registerConverter(new DocumentToArrayNodeConverter(mapper));
        adapter.registerConverter(new ArrayNodeToDocumentConverter());
    }
}
