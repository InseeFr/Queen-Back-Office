package fr.insee.queen.jms.service.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.jms.exception.PropertyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PropertyValidatorTest {


    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static JsonNode json(String raw) {
        try {
            return MAPPER.readTree(raw);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Group of tests for successful / valid cases
    @Nested
    @DisplayName("Success cases")
    class SuccessCases {

        @Test
        @DisplayName("Returns text value when the field is present and textual")
        void returnsTextValueWhenPresentAndTextual() throws Exception {
            // Given a JSON node with a valid string property
            JsonNode node = json("{\"name\":\"Insee\"}");

            // When calling textValue on this property
            String value = PropertyValidator.textValue(node, "name");

            // Then it should return the corresponding string value
            assertEquals("Insee", value);
        }

        @Test
        @DisplayName("Accepts empty string (current behavior)")
        void acceptsEmptyStringCurrently() throws Exception {
            // Given a JSON node with an empty string property
            JsonNode node = json("{\"empty\":\"\"}");

            // When calling textValue
            String value = PropertyValidator.textValue(node, "empty");

            // Then it should return an empty string without throwing an exception
            assertEquals("", value);
        }
    }

    // Group of tests for errors related to missing or null values
    @Nested
    @DisplayName("Presence / value errors")
    class PresenceErrors {

        @Test
        @DisplayName("Missing field -> PropertyException")
        void missingFieldThrows() {
            // Given a JSON node without the requested property
            JsonNode node = json("{\"name\":\"Insee\"}");

            // When trying to access a missing field
            // Then a PropertyException should be thrown
            PropertyException ex = assertThrows(
                    PropertyException.class,
                    () -> PropertyValidator.textValue(node, "missing")
            );

            // The exception message should indicate the missing field
            assertEquals("Missing or null field : 'missing'", ex.getMessage());
        }

        @Test
        @DisplayName("Explicit JSON null -> PropertyException")
        void explicitJsonNullThrows() {
            // Given a JSON node where the field is explicitly null
            JsonNode node = json("{\"name\":null}");

            // When calling textValue on this field
            // Then a PropertyException should be thrown
            PropertyException ex = assertThrows(
                    PropertyException.class,
                    () -> PropertyValidator.textValue(node, "name")
            );

            // The exception message should clearly indicate the issue
            assertEquals("Missing or null field : 'name'", ex.getMessage());
        }

        @Test
        @DisplayName("String literal 'null' -> PropertyException")
        void stringLiteralNullThrows() {
            // Given a JSON node where the field value is the literal string "null"
            JsonNode node = json("{\"name\":\"null\"}");

            // When calling textValue on this field
            // Then it should be treated as invalid and throw a PropertyException
            PropertyException ex = assertThrows(
                    PropertyException.class,
                    () -> PropertyValidator.textValue(node, "name")
            );

            // The exception message should match the expected pattern
            assertEquals("Missing or null field : 'name'", ex.getMessage());
        }
    }

    // Group of tests for type errors (non-textual values)
    @Nested
    @DisplayName("Type errors (non-textual values)")
    class TypeErrors {

        @Test
        @DisplayName("NUMBER -> PropertyException with type NUMBER")
        void numberTypeThrows() {
            JsonNode node = json("{\"age\": 42}");
            PropertyException ex = assertThrows(
                    PropertyException.class,
                    () -> PropertyValidator.textValue(node, "age")
            );
            // The error must mention that the field must be a string and show the detected node type
            assertTrue(ex.getMessage().contains("must be a string (type found : NUMBER)"));
        }

        @Test
        @DisplayName("BOOLEAN -> PropertyException with type BOOLEAN")
        void booleanTypeThrows() {
            JsonNode node = json("{\"flag\": true}");
            PropertyException ex = assertThrows(
                    PropertyException.class,
                    () -> PropertyValidator.textValue(node, "flag")
            );
            assertTrue(ex.getMessage().contains("type found : BOOLEAN"));
        }

        @Test
        @DisplayName("OBJECT -> PropertyException with type OBJECT")
        void objectTypeThrows() {
            JsonNode node = json("{\"meta\": {\"k\":\"v\"}}");
            PropertyException ex = assertThrows(
                    PropertyException.class,
                    () -> PropertyValidator.textValue(node, "meta")
            );
            assertTrue(ex.getMessage().contains("type found : OBJECT"));
        }

        @Test
        @DisplayName("ARRAY -> PropertyException with type ARRAY")
        void arrayTypeThrows() {
            JsonNode node = json("{\"list\": [\"a\",\"b\"]}");
            PropertyException ex = assertThrows(
                    PropertyException.class,
                    () -> PropertyValidator.textValue(node, "list")
            );
            assertTrue(ex.getMessage().contains("type found : ARRAY"));
        }

        @Test
        @DisplayName("BINARY -> PropertyException with type BINARY")
        void binaryTypeThrows() throws Exception {
            // Build a binary node via ObjectMapper (base64 in JSON becomes BINARY)
            byte[] bytes = new byte[] {1, 2, 3};
            ObjectNode node = MAPPER.createObjectNode();
            node.put("bin", Base64.getEncoder().encodeToString(bytes)); // <- still TEXTUAL unless we force binary
            // To really get BINARY, write a BinaryNode:
            node.set("bin", new BinaryNode(bytes));

            PropertyException ex = assertThrows(
                    PropertyException.class,
                    () -> PropertyValidator.textValue(node, "bin")
            );
            assertTrue(ex.getMessage().contains("type found : BINARY"));
        }

//        @Test
//        @DisplayName("POJO -> PropertyException with type POJO")
//        void pojoTypeThrows() {
//            // Create a POJO node explicitly
//            Object pojo = new Object() { int v = 1; };
//            ObjectNode root = MAPPER.createObjectNode();
//            root.set("pojo", MAPPER.valueToTree(pojo)); // valueToTree returns OBJECT, not POJO, in most cases
//            // To ensure POJO node: use POJONode directly
//            ObjectNode node = MAPPER.createObjectNode();
//            node.set("pojo", com.fasterxml.jackson.databind.node.POJONode.construct(pojo));
//
//            PropertyException ex = assertThrows(
//                    PropertyException.class,
//                    () -> PropertyValidator.textValue(node, "pojo")
//            );
//            assertTrue(ex.getMessage().contains("type found : POJO"));
//        }

        @Test
        @DisplayName("MISSING node doesn't reach type check (handled earlier)")
        void missingNodeDoesNotReachTypeCheck() {
            JsonNode node = json("{\"x\":1}");
            PropertyException ex = assertThrows(
                    PropertyException.class,
                    () -> PropertyValidator.textValue(node, "absent")
            );
            // This validates the earlier branch; not the type message.
            assertEquals("Missing or null field : 'absent'", ex.getMessage());
        }

        @Test
        @DisplayName("Text that looks like a number is allowed (TEXTUAL)")
        void numericStringIsAllowed() throws Exception {
            JsonNode node = json("{\"code\":\"1234\"}");
            assertEquals("1234", PropertyValidator.textValue(node, "code"));
        }
    }


}