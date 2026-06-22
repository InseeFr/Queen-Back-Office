package fr.insee.queen.application.web.validation.json;


import com.networknt.schema.*;
import com.networknt.schema.Error;
import fr.insee.queen.application.web.validation.exception.JsonValidatorComponentInitializationException;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class JsonValidatorComponent {

    private static final ClassLoader CLASS_LOADER = JsonValidatorComponent.class.getClassLoader();
    private static final String SCHEMA_RESOURCE_DIR = "static/v3/";
    private static final String SCHEMA_BASE_URI = "https://insee.fr/";

    private final Map<SchemaType, Schema> schemas;

    public JsonValidatorComponent() {
        schemas = loadSchemas();
    }

    /** Loads the JSON schemas defined in the app resources. */
    private static @NonNull Map<SchemaType, Schema> loadSchemas() {
        Map<SchemaType, Schema> schemas = new EnumMap<>(SchemaType.class);

        // Plumbing methods related to the json validation lib:
        SchemaRegistry schemaRegistry = buildSchemaRegistry();
        registerSchemas(schemaRegistry, schemas);
        initializeValidators(schemas);

        return schemas;
    }

    /** Instantiates the schema registry.
     * @see SchemaRegistry */
    private static SchemaRegistry buildSchemaRegistry() {
        return SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12,
                builder -> builder
                        .schemaIdResolvers(schemaIdResolvers -> schemaIdResolvers
                                .mapPrefix(SCHEMA_BASE_URI, "classpath:" + SCHEMA_RESOURCE_DIR)));
    }

    /** Parses schemas defined in the schemas types enum.
     * @see SchemaType */
    private static void registerSchemas(SchemaRegistry schemaRegistry, Map<SchemaType, Schema> schemas) {
        for(SchemaType schemaType : SchemaType.values()) {
            try(InputStream schemaStream = openStream(schemaType)) {
                String schemaIri = SCHEMA_BASE_URI + schemaType.getSchemaFileName();
                Schema schema = schemaRegistry.getSchema(SchemaLocation.of(schemaIri), schemaStream, InputFormat.JSON);
                schemas.put(schemaType, schema);
            } catch (IOException e) {
                throw new JsonValidatorComponentInitializationException(
                        "Failed to load schema " + schemaType.getSchemaFileName(), e);
            }
        }
    }

    private static InputStream openStream(SchemaType schemaType) {
        InputStream schemaStream = CLASS_LOADER.getResourceAsStream(SCHEMA_RESOURCE_DIR + schemaType.getSchemaFileName());
        if (schemaStream == null) {
            throw new JsonValidatorComponentInitializationException(
                    "Schema not found: " + schemaType.getSchemaFileName());
        }
        return schemaStream;
    }

    /** Enforces preloading of validation schemas. */
    private static void initializeValidators(Map<SchemaType, Schema> schemas) {
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
