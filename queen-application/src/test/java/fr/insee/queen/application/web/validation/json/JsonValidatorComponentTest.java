package fr.insee.queen.application.web.validation.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.ValidatorTypeCode;
import fr.insee.queen.application.utils.JsonTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JsonValidatorComponentTest {

    private JsonValidatorComponent validatorComponent;
    private ObjectMapper mapper;

    @BeforeEach
    void init() {
        this.mapper = new ObjectMapper();
        this.validatorComponent = new JsonValidatorComponent();
    }

    @Test
    @DisplayName("when validating valid data, no errors returned")
    void testData01() throws IOException {
        String dataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/data/valid-data.json");
        JsonNode dataNode = mapper.readValue(dataJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.DATA, dataNode);
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("when validating incorrect value from collected data, errors returned")
    void testData02() throws IOException {
        String dataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/data/invalid-value-collected-data.json");
        JsonNode dataNode = mapper.readValue(dataJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.DATA, dataNode);
        assertThat(errors).isNotEmpty();
        errors.forEach(error -> assertBadType(error, "$.COLLECTED.VALUE.EDITED"));
    }

    @Test
    @DisplayName("when validating incorrect collected data, errors returned")
    void testData03() throws IOException {
        String dataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/data/invalid-property-collected-data.json");
        JsonNode dataNode = mapper.readValue(dataJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.DATA, dataNode);
        assertThat(errors).isNotEmpty();
        errors.forEach(error -> assertForbiddenProperty(error, "$.COLLECTED.VALUE", "PLOP"));
    }

    @ParameterizedTest
    @CsvSource({"json-schema-validation/data/invalid-value-calculated-data.json,CALCULATED","json-schema-validation/data/invalid-value-external-data.json,EXTERNAL"})
    @DisplayName("when validating incorrect value from calculated/external data, errors returned")
    void testData04(String filePath, String targettedObject) throws IOException {
        String dataJson = JsonTestHelper.getResourceFileAsString(filePath);
        JsonNode dataNode = mapper.readValue(dataJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.DATA, dataNode);
        assertThat(errors).isNotEmpty();
        errors.forEach(error -> assertBadType(error, "$."+targettedObject+".OBJECT_VAR"));
    }

    @Test
    @DisplayName("when validating incorrect property from data, errors returned")
    void testData05() throws IOException {
        String dataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/data/invalid-property-data.json");
        JsonNode dataNode = mapper.readValue(dataJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.DATA, dataNode);
        assertThat(errors).isNotEmpty();
        errors.forEach(error -> assertForbiddenProperty(error, "$", "PLOP"));
    }

    @Test
    @DisplayName("when validating valid personalization, no errors returned")
    void testPersonalization01() throws IOException {
        String personalizationJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/personalization/valid-personalization.json");
        JsonNode personalizationNode = mapper.readValue(personalizationJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.PERSONALIZATION, personalizationNode);
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("when validating invalid personalization, return json schema errors")
    void testPersonalization02() throws IOException {
        String personalizationJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/personalization/invalid-personalization.json");
        JsonNode personalizationNode = mapper.readValue(personalizationJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.PERSONALIZATION, personalizationNode);
        assertThat(errors).hasSize(5);

        ValidationMessage[] messages = errors.toArray(ValidationMessage[]::new);

        ValidationMessage error = messages[0];
        assertBadType(error, "$[0].name");

        error = messages[1];
        assertBadType(error, "$[0].value");

        error = messages[2];
        assertForbiddenProperty(error, "$[0]", "forbidden_property");

        error = messages[3];
        assertRequiredProperty(error, "$[2]", "name");

        error = messages[4];
        assertRequiredProperty(error, "$[2]", "value");
    }

    @Test
    @DisplayName("when validating valid nomenclature, no errors returned")
    void testNomenclature01() throws IOException {
        String nomenclatureJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/nomenclature/valid-nomenclature.json");
        JsonNode nomenclatureNode = mapper.readValue(nomenclatureJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.NOMENCLATURE, nomenclatureNode);
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("when validating invalid nomenclature, return json schema errors")
    void testNomenclature02() throws IOException {
        String nomenclatureJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/nomenclature/invalid-nomenclature.json");
        JsonNode nomenclatureNode = mapper.readValue(nomenclatureJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.NOMENCLATURE, nomenclatureNode);
        assertThat(errors).hasSize(4);

        ValidationMessage[] messages = errors.toArray(ValidationMessage[]::new);

        ValidationMessage error = messages[0];
        assertRequiredProperty(error, "$[0]", "id");

        error = messages[1];
        assertRequiredProperty(error, "$[0]", "label");

        error = messages[2];
        assertBadType(error, "$[1].id");

        error = messages[3];
        assertBadType(error, "$[1].label");
    }

    @Test
    @DisplayName("when validating valid metadata, no errors returned")
    void testMetadata01() throws IOException {
        String metadataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/metadata/valid-metadata.json");
        JsonNode metadataNode = mapper.readValue(metadataJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.METADATA, metadataNode);
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("when validating invalid metadata, return json schema errors")
    void testMetadata02() throws IOException {
        String metadataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/metadata/invalid-metadata.json");
        JsonNode metadataNode = mapper.readValue(metadataJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.METADATA, metadataNode);
        assertThat(errors).hasSize(6);

        ValidationMessage[] messages = errors.toArray(ValidationMessage[]::new);

        ValidationMessage error = messages[0];
        assertBadPattern(error, "$.inseeContext");

        error = messages[1];
        assertBadType(error, "$.variables[1].name");

        error = messages[2];
        assertBadType(error, "$.variables[1].value");

        error = messages[3];
        assertRequiredProperty(error, "$.variables[2]", "name");

        error = messages[4];
        assertRequiredProperty(error, "$.variables[2]", "value");

        error = messages[5];
        assertForbiddenProperty(error, "$.variables[2]", "forbidden-property");
    }

    private void assertBadPattern(ValidationMessage error, String instanceLocation) {
        assertThat(error.getInstanceLocation()).hasToString(instanceLocation);
        ValidatorTypeCode typeCode = ValidatorTypeCode.fromValue(error.getType());
        assertThat(typeCode).isEqualTo(ValidatorTypeCode.PATTERN);
    }

    private void assertBadType(ValidationMessage error, String instanceLocation) {
        assertThat(error.getInstanceLocation()).hasToString(instanceLocation);
        ValidatorTypeCode typeCode = ValidatorTypeCode.fromValue(error.getType());
        assertThat(typeCode).isEqualTo(ValidatorTypeCode.TYPE);
    }

    private void assertForbiddenProperty(ValidationMessage error, String instanceLocation, String forbiddenProperty) {
        assertThat(error.getInstanceLocation()).hasToString(instanceLocation);
        assertThat(error.getProperty()).isEqualTo(forbiddenProperty);
        ValidatorTypeCode typeCode = ValidatorTypeCode.fromValue(error.getType());
        assertThat(typeCode).isEqualTo(ValidatorTypeCode.ADDITIONAL_PROPERTIES);
    }

    private void assertRequiredProperty(ValidationMessage error, String instanceLocation, String requiredProperty) {
        assertThat(error.getInstanceLocation()).hasToString(instanceLocation);
        assertThat(error.getProperty()).isEqualTo(requiredProperty);
        ValidatorTypeCode typeCode = ValidatorTypeCode.fromValue(error.getType());
        assertThat(typeCode).isEqualTo(ValidatorTypeCode.REQUIRED);
    }
}
