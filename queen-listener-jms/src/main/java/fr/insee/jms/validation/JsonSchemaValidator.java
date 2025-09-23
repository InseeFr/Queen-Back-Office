package fr.insee.jms.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.networknt.schema.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public final class JsonSchemaValidator {
    private JsonSchemaValidator() {}

    /** Tu as DÉJÀ un JsonSchema instancié */
    public static <T> T readAndValidate(JsonNode root,
                                        JsonSchema schema,
                                        Class<T> targetType,
                                        ObjectMapper mapper)
            throws SchemaValidationException, JsonProcessingException {
        ensureValid(root, schema);
        return mapper.treeToValue(root, targetType);
    }

    /** Tu donnes un chemin CLASSPATH (ex: "schemas/process-message.schema.json") */
    public static <T> T readAndValidateFromClasspath(JsonNode root,
                                                     String schemaResourcePath,
                                                     Class<T> targetType,
                                                     ObjectMapper mapper)
            throws SchemaValidationException, IOException {
        JsonSchema schema = loadSchemaFromClasspath(schemaResourcePath, mapper);
        return readAndValidate(root, schema, targetType, mapper);
    }

    public static JsonSchema loadSchemaFromClasspath(String resourcePath, ObjectMapper mapper) throws IOException {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new IOException("Schema resource path is null or blank");
        }
        String cp = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(cp)) {
            if (in == null) throw new IOException("Schema not found on classpath: " + resourcePath);
            JsonNode schemaNode = mapper.readTree(in);
            return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909).getSchema(schemaNode);
        }
    }

    public static final class SchemaValidationException extends Exception {
        private final java.util.Set<ValidationMessage> errors;
        public SchemaValidationException(String msg, java.util.Set<ValidationMessage> errors) { super(msg); this.errors = errors; }
        public java.util.Set<ValidationMessage> getErrors() { return errors; }
    }

    private static void ensureValid(JsonNode root, JsonSchema schema) throws SchemaValidationException {
        log.info("Schéma Appliqué : " + schema.getId());
        java.util.Set<ValidationMessage> errors = schema.validate(root);
        if (!errors.isEmpty()) {
            String formatted = errors.stream()
                    .sorted(java.util.Comparator.comparing(ValidationMessage::getEvaluationPath))
                    .map(err -> err.getMessage())
                    .collect(java.util.stream.Collectors.joining("\n"));
            throw new SchemaValidationException(
                    "Uploaded JSON is not correct according to the json-schema:\n" + formatted, errors);
        } else {
            log.info("JSON conforme au schéma : " + schema.getId());
        }
    }
}
