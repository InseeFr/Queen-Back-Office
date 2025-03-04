package fr.insee.queen.infrastructure.db.data.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ObjectNodeConverterTest {

    private final ObjectNodeConverter converter = new ObjectNodeConverter();

    @Test
    @DisplayName("Should convert ObjectNode to JSON string")
    void convertToDatabaseColumn_shouldConvertObjectNodeToJsonString() {
        // Given
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("key", "value");

        // When
        String result = converter.convertToDatabaseColumn(objectNode);

        // Then
        assertThat(result).isEqualTo("{\"key\":\"value\"}");
    }

    @Test
    @DisplayName("Should return null when converting null ObjectNode to database column")
    void convertToDatabaseColumn_shouldReturnNullWhenObjectNodeIsNull() {
        // Given
        ObjectNode objectNode = null;

        // When
        String result = converter.convertToDatabaseColumn(objectNode);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert JSON string to ObjectNode")
    void convertToEntityAttribute_shouldConvertJsonStringToObjectNode() {
        // Given
        String json = "{\"key\":\"value\"}";

        // When
        ObjectNode result = converter.convertToEntityAttribute(json);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("key").asText()).isEqualTo("value");
    }

    @Test
    @DisplayName("Should return null when converting null JSON string to ObjectNode")
    void convertToEntityAttribute_shouldReturnNullWhenJsonStringIsNull() {
        // Given
        String json = null;

        // When
        ObjectNode result = converter.convertToEntityAttribute(json);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when JSON string to ObjectNode conversion fails")
    void convertToEntityAttribute_shouldThrowExceptionOnInvalidJsonString() {
        // Given
        String invalidJson = "invalid json";

        // When / Then
        assertThatThrownBy(() -> converter.convertToEntityAttribute(invalidJson))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ObjectNodeConverter.STRING_TO_OBJECTNODE_ERROR_MESSAGE);
    }
}
