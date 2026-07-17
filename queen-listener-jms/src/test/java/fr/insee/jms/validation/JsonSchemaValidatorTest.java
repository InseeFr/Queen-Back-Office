package fr.insee.jms.validation;

import com.networknt.schema.Schema;
import fr.insee.queen.jms.exception.SchemaValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonSchemaValidatorTest {

    private final JsonMapper jsonMapper = new JsonMapper();
    private static final String TEST_SCHEMA = "/schemas/test-schema.json";

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Throws IOException when the resource path is null or blank")
    void throwsWhenPathIsNullOrBlank(String path) {
        assertThatThrownBy(() -> JsonSchemaValidator.loadSchemaFromClasspath(path, jsonMapper))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("null or blank");
    }

    @Test
    @DisplayName("Throws IOException when the resource does not exist on the classpath")
    void throwsWhenResourceIsMissing() {
        String missingResource = "/does-not-exist.json";

        assertThatThrownBy(() -> JsonSchemaValidator.loadSchemaFromClasspath(missingResource, jsonMapper))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Schema not found on classpath")
                .hasMessageContaining(missingResource);
    }

    @Test
    @DisplayName("Loads a schema from classpath when path does not start with /")
    void loadsSchemaFromClasspathWithoutLeadingSlash() throws IOException {
        // Given
        String pathWithoutLeadingSlash = "schemas/test-schema.json";

        // When
        Schema schema = JsonSchemaValidator.loadSchemaFromClasspath(pathWithoutLeadingSlash, jsonMapper);

        // Then
        assertThat(schema).isNotNull();
    }

    @Test
    @DisplayName("readAndValidate throws SchemaValidationException when JSON does not match the schema")
    void readAndValidateThrowsForInvalidJson() throws IOException {
        // Given
        Schema schema = JsonSchemaValidator.loadSchemaFromClasspath(TEST_SCHEMA, jsonMapper);
        JsonNode invalidJson = jsonMapper.readTree("{\"other\": 42}");

        // When / Then
        assertThatThrownBy(() -> JsonSchemaValidator.readAndValidate(invalidJson, schema, Object.class, jsonMapper))
                .isInstanceOf(SchemaValidationException.class);
    }

    @Test
    @DisplayName("readAndValidateFromClasspath returns mapped object for a valid resource and matching JSON")
    void readAndValidateFromClasspathPassesForMatchingJson() throws IOException, SchemaValidationException {
        // Given
        JsonNode validJson = jsonMapper.readTree("{\"name\": \"hello\"}");

        // When
        Object result = JsonSchemaValidator.readAndValidateFromClasspath(validJson, TEST_SCHEMA, Object.class, jsonMapper);

        // Then
        assertThat(result).isNotNull();
    }
}
