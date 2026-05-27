package fr.insee.jms.validation;

import com.networknt.schema.Error;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import fr.insee.queen.jms.exception.SchemaValidationException;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public final class JsonSchemaValidator {
    private JsonSchemaValidator() {}

    public static <T> T readAndValidate(JsonNode root,
                                        Schema schema,
                                        Class<T> targetType,
                                        ObjectMapper mapper)
            throws SchemaValidationException, JacksonException {
        ensureValid(root, schema);
        return mapper.treeToValue(root, targetType);
    }

    public static <T> T readAndValidateFromClasspath(JsonNode root,
                                                     String schemaResourcePath,
                                                     Class<T> targetType,
                                                     ObjectMapper mapper)
            throws SchemaValidationException, IOException {
        Schema schema = loadSchemaFromClasspath(schemaResourcePath, mapper);
        return readAndValidate(root, schema, targetType, mapper);
    }

    public static Schema loadSchemaFromClasspath(String resourcePath, ObjectMapper mapper) throws IOException {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new IOException("Schema resource path is null or blank");
        }
        String cp = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(cp)) {
            if (in == null) throw new IOException("Schema not found on classpath: " + resourcePath);
            JsonNode schemaNode = mapper.readTree(in);
            return SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12).getSchema(schemaNode);
        }
    }

    private static void ensureValid(JsonNode root, Schema schema) throws SchemaValidationException {
        log.info("Schéma Apply : {}", schema.getSchemaNode().get("title"));
        java.util.List<Error> errors = schema.validate(root);
        if (!errors.isEmpty()) {
            String formatted = errors.stream()
                    .sorted(java.util.Comparator.comparing(Error::getEvaluationPath))
                    .map(Error::getMessage)
                    .collect(java.util.stream.Collectors.joining("\n"));
            throw new SchemaValidationException(
                    "Uploaded JSON is not correct according to the json-schema:\n" + formatted, errors);
        }
        log.info("Schema-compliant JSON : {}", schema.getSchemaNode().get("title"));
    }
}
