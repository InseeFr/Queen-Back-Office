package fr.insee.queen.application.web.validation.json;


import com.networknt.schema.*;
import com.networknt.schema.Error;
import fr.insee.queen.application.web.validation.exception.JsonValidatorComponentInitializationException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class JsonValidatorComponent {

    private static final String SCHEMA_BASE_URI = "https://insee.fr/";
    private final Map<SchemaType, Schema> schemas;

    public JsonValidatorComponent() {
        SchemaRegistry schemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12,
                builder -> builder
                        .schemaIdResolvers(schemaIdResolvers -> schemaIdResolvers
                                .mapPrefix(SCHEMA_BASE_URI, "classpath:static/v3/")));
        schemas = new EnumMap<>(SchemaType.class);

        ClassLoader classLoader = this.getClass().getClassLoader();

        for(SchemaType schemaType : SchemaType.values()) {

            try(InputStream schemaStream = classLoader.getResourceAsStream( "static/v3/" + schemaType.getSchemaFileName())) {
                String schemaIri = SCHEMA_BASE_URI + schemaType.getSchemaFileName();
                Schema schema = schemaRegistry.getSchema(SchemaLocation.of(schemaIri), schemaStream, InputFormat.JSON);
                schemas.put(schemaType, schema);
            } catch (IOException e) {
                throw new JsonValidatorComponentInitializationException(e.getMessage());
            }
        }

        for (SchemaType schemaType : SchemaType.values()) {
            schemas.get(schemaType).initializeValidators();
        }
    }

    // Note: this method signature exposes com.networkn json-schema-validator internals
    // Defining a 'JsonError' class for this project would improve maintainability
    public List<Error> validate(SchemaType schemaType, JsonNode value) {
        Schema schema = schemas.get(schemaType);
        return schema.validate(value);
    }

}
