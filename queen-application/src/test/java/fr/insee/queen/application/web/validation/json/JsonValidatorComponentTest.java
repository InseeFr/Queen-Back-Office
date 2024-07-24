package fr.insee.queen.application.web.validation.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
        assertThat(errors)
                .isNotEmpty()
                .anySatisfy(error -> {
                    assertBadType(error, "$.COLLECTED.VALUE.EDITED");
                })
                .anySatisfy(error -> {
                    assertBadType(error, "$.COLLECTED.VALUE.COLLECTED");
                });
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
        assertThat(errors)
                .isNotEmpty()
                .anySatisfy(error -> {
                    assertBadType(error, "$."+targettedObject+".OBJECT_VAR");
                })
                .anySatisfy(error -> {
                    assertBadType(error, "$."+targettedObject+".PAIRWISE");
                });
    }

    @Test
    @DisplayName("when validating incorrect property from data, errors returned")
    void testData05() throws IOException {
        String dataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/data/invalid-property-data.json");
        JsonNode dataNode = mapper.readValue(dataJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.DATA, dataNode);
        assertThat(errors).isEmpty();
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
        assertThat(errors).hasSize(5);

        ValidationMessage[] messages = errors.toArray(ValidationMessage[]::new);

        ValidationMessage error = messages[0];
        assertBadEnum(error, "$.inseeContext");

        error = messages[1];
        assertBadType(error, "$.variables[1].name");

        error = messages[2];
        assertRequiredProperty(error, "$.variables[2]", "name");

        error = messages[3];
        assertRequiredProperty(error, "$.variables[2]", "value");

        error = messages[4];
        assertForbiddenProperty(error, "$.variables[2]", "forbidden-property");
    }

    @Test
    @DisplayName("when validating valid survey unit temp zone, no errors returned")
    void testTempZone01() throws IOException {
        String surveyUnitTempZoneJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/surveyunittempzone/valid-tempzone.json");
        JsonNode surveyUnitTempZoneNode = mapper.readValue(surveyUnitTempZoneJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.SURVEY_UNIT_TEMP_ZONE, surveyUnitTempZoneNode);
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("when validating survey unit temp zone with missing attributes, return json schema errors")
    void testTempZone02() {
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.SURVEY_UNIT_TEMP_ZONE, JsonNodeFactory.instance.objectNode());
        assertThat(errors).hasSize(3);

        ValidationMessage[] messages = errors.toArray(ValidationMessage[]::new);

        ValidationMessage error = messages[0];
        assertRequiredProperty(error, "$", "data");

        error = messages[1];
        assertRequiredProperty(error, "$", "stateData");

        error = messages[2];
        assertRequiredProperty(error, "$", "questionnaireId");
    }

    @Test
    @DisplayName("when validating survey unit temp zone with missing stateData attributes, return json schema errors")
    void testTempZone03() throws IOException {
        String surveyUnitTempZoneJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/surveyunittempzone/invalid-missing-statedata-attributes-tempzone.json");
        JsonNode surveyUnitTempZoneNode = mapper.readValue(surveyUnitTempZoneJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.SURVEY_UNIT_TEMP_ZONE, surveyUnitTempZoneNode);
        assertThat(errors).hasSize(3);

        ValidationMessage[] messages = errors.toArray(ValidationMessage[]::new);

        ValidationMessage error = messages[0];
        assertRequiredProperty(error, "$.stateData", "date");

        error = messages[1];
        assertRequiredProperty(error, "$.stateData", "state");

        error = messages[2];
        assertRequiredProperty(error, "$.stateData", "currentPage");
    }

    @Test
    @DisplayName("when validating survey unit temp zone with incorrect types, return json schema errors")
    void testTempZone04() throws IOException {
        String surveyUnitTempZoneJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/surveyunittempzone/invalid-types.json");
        JsonNode surveyUnitTempZoneNode = mapper.readValue(surveyUnitTempZoneJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.SURVEY_UNIT_TEMP_ZONE, surveyUnitTempZoneNode);
        assertThat(errors).hasSize(7);

        ValidationMessage[] messages = errors.toArray(ValidationMessage[]::new);

        ValidationMessage error = messages[0];
        assertBadType(error, "$.comment");

        error = messages[1];
        assertBadType(error, "$.questionnaireId");

        error = messages[2];
        assertBadType(error, "$.stateData.date");

        error = messages[3];
        assertBadEnum(error, "$.stateData.state");

        error = messages[4];
        assertBadType(error, "$.stateData.currentPage");

        error = messages[5];
        assertForbiddenProperty(error, "$.stateData", "forbidden-property");

        error = messages[6];
        assertForbiddenProperty(error, "$", "forbidden-property");
    }

    @Test
    @DisplayName("when validating survey unit temp zone with incorrect attributes length, return json schema errors")
    void testTempZone05() throws IOException {
        String surveyUnitTempZoneJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/surveyunittempzone/invalid-lengths.json");
        JsonNode surveyUnitTempZoneNode = mapper.readValue(surveyUnitTempZoneJson, JsonNode.class);
        Set<ValidationMessage> errors = validatorComponent.validate(SchemaType.SURVEY_UNIT_TEMP_ZONE, surveyUnitTempZoneNode);
        assertThat(errors).hasSize(2);

        ValidationMessage[] messages = errors.toArray(ValidationMessage[]::new);

        ValidationMessage error = messages[0];
        assertBadLength(error, "$.questionnaireId");

        error = messages[1];
        assertBadLength(error, "$.stateData.currentPage");
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

    private void assertBadEnum(ValidationMessage error, String instanceLocation) {
        assertThat(error.getInstanceLocation()).hasToString(instanceLocation);
        ValidatorTypeCode typeCode = ValidatorTypeCode.fromValue(error.getType());
        assertThat(typeCode).isEqualTo(ValidatorTypeCode.ENUM);
    }

    private void assertBadLength(ValidationMessage error, String instanceLocation) {
        assertThat(error.getInstanceLocation()).hasToString(instanceLocation);
        ValidatorTypeCode typeCode = ValidatorTypeCode.fromValue(error.getType());
        assertThat(typeCode).isEqualTo(ValidatorTypeCode.MIN_LENGTH);
    }
}
