package fr.insee.queen.application.web.validation.json;

import com.networknt.schema.Error;
import fr.insee.queen.application.utils.JsonTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.JsonNodeFactory;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JsonValidatorComponentTest {

    private JsonValidatorComponent validatorComponent;
    private ObjectMapper mapper;

    // Note: the assertions like "is equal to '/0/name'" are implementation specific with the JSON validation lib used
    // should probably be reworked.

    @BeforeEach
    void init() {
        this.mapper = new JsonMapper();
        this.validatorComponent = new JsonValidatorComponent();
    }

    @Test
    @DisplayName("when validating valid data, no errors returned")
    void testData01() throws IOException {
        String dataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/data/valid-data.json");
        JsonNode dataNode = mapper.readValue(dataJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.DATA, dataNode);
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("when validating incorrect value from collected data, errors returned")
    @Disabled(value = "Disabled temporarily for lunatic bug")
    //TODO enable tests when lunatic bug fixed
    void testData02() throws IOException {
        String dataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/data/invalid-value-collected-data.json");
        JsonNode dataNode = mapper.readValue(dataJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.DATA, dataNode);
        assertThat(errors)
                .isNotEmpty()
                .anySatisfy(error -> {
                    assertBadType(error, "/COLLECTED.VALUE.EDITED");
                })
                .anySatisfy(error -> {
                    assertBadType(error, "/COLLECTED.VALUE.COLLECTED");
                });
    }

    @Test
    @DisplayName("when validating incorrect collected data, errors returned")
    void testData03() throws IOException {
        String dataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/data/invalid-property-collected-data.json");
        JsonNode dataNode = mapper.readValue(dataJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.DATA, dataNode);
        assertThat(errors).isNotEmpty();
        errors.forEach(error -> assertForbiddenProperty(error, "/COLLECTED/VALUE", "PLOP"));
    }

    @ParameterizedTest
    @CsvSource({"json-schema-validation/data/invalid-value-calculated-data.json,CALCULATED","json-schema-validation/data/invalid-value-external-data.json,EXTERNAL"})
    @DisplayName("when validating incorrect value from calculated/external data, errors returned")
    @Disabled(value = "Disabled temporarily for lunatic bug")
    //TODO enable tests when lunatic bug fixed
    void testData04(String filePath, String targettedObject) throws IOException {
        String dataJson = JsonTestHelper.getResourceFileAsString(filePath);
        JsonNode dataNode = mapper.readValue(dataJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.DATA, dataNode);
        assertThat(errors)
                .isNotEmpty()
                .anySatisfy(error -> {
                    assertBadType(error, "/"+targettedObject+".OBJECT_VAR");
                })
                .anySatisfy(error -> {
                    assertBadType(error, "/"+targettedObject+".PAIRWISE");
                });
    }

    @Test
    @DisplayName("when validating incorrect property from data, errors returned")
    void testData05() throws IOException {
        String dataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/data/invalid-property-data.json");
        JsonNode dataNode = mapper.readValue(dataJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.DATA, dataNode);
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("when validating valid personalization, no errors returned")
    void testPersonalization01() throws IOException {
        String personalizationJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/personalization/valid-personalization.json");
        JsonNode personalizationNode = mapper.readValue(personalizationJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.PERSONALIZATION, personalizationNode);
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("when validating invalid personalization, return json schema errors")
    void testPersonalization02() throws IOException {
        String personalizationJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/personalization/invalid-personalization.json");
        JsonNode personalizationNode = mapper.readValue(personalizationJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.PERSONALIZATION, personalizationNode);
        assertThat(errors).hasSize(5);
        assertBadType(errors.get(0), "/0/name");
        assertBadType(errors.get(1), "/0/value");
        assertForbiddenProperty(errors.get(2), "/0", "forbidden_property");
        assertRequiredProperty(errors.get(3), "/2", "name");
        assertRequiredProperty(errors.get(4), "/2", "value");
    }

    @Test
    @DisplayName("when validating valid nomenclature, no errors returned")
    void testNomenclature01() throws IOException {
        String nomenclatureJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/nomenclature/valid-nomenclature.json");
        JsonNode nomenclatureNode = mapper.readValue(nomenclatureJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.NOMENCLATURE, nomenclatureNode);
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("when validating invalid nomenclature, return json schema errors")
    void testNomenclature02() throws IOException {
        String nomenclatureJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/nomenclature/invalid-nomenclature.json");
        JsonNode nomenclatureNode = mapper.readValue(nomenclatureJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.NOMENCLATURE, nomenclatureNode);
        assertThat(errors).hasSize(4);
        assertRequiredProperty(errors.get(0), "/0", "id");
        assertRequiredProperty(errors.get(1), "/0", "label");
        assertBadType(errors.get(2), "/1/id");
        assertBadType(errors.get(3), "/1/label");
    }

    @Test
    @DisplayName("when validating valid metadata, no errors returned")
    void testMetadata01() throws IOException {
        String metadataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/metadata/valid-metadata.json");
        JsonNode metadataNode = mapper.readValue(metadataJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.METADATA, metadataNode);
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("when validating invalid metadata, return json schema errors")
    void testMetadata02() throws IOException {
        String metadataJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/metadata/invalid-metadata.json");
        JsonNode metadataNode = mapper.readValue(metadataJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.METADATA, metadataNode);
        assertThat(errors).hasSize(5);
        assertBadEnum(errors.get(0), "/inseeContext");
        assertBadType(errors.get(1), "/variables/1/name");
        assertRequiredProperty(errors.get(2), "/variables/2", "name");
        assertRequiredProperty(errors.get(3), "/variables/2", "value");
        assertForbiddenProperty(errors.get(4), "/variables/2", "forbidden-property");
    }

    @Test
    @DisplayName("when validating valid interrogation temp zone, no errors returned")
    void testTempZone01() throws IOException {
        String interrogationTempZoneJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/interrogationtempzone/valid-tempzone.json");
        JsonNode interrogationTempZoneNode = mapper.readValue(interrogationTempZoneJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.INTERROGATION_TEMP_ZONE, interrogationTempZoneNode);
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("when validating interrogation temp zone with missing attributes, return json schema errors")
    void testTempZone02() {
        List<Error> errors = validatorComponent.validate(SchemaType.INTERROGATION_TEMP_ZONE, JsonNodeFactory.instance.objectNode());
        assertThat(errors).hasSize(3);
        assertRequiredProperty(errors.get(0), "", "data");
        assertRequiredProperty(errors.get(1), "", "stateData");
        assertRequiredProperty(errors.get(2), "", "questionnaireId");
    }

    @Test
    @DisplayName("when validating interrogation temp zone with missing stateData attributes, return json schema errors")
    void testTempZone03() throws IOException {
        String interrogationTempZoneJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/interrogationtempzone/invalid-missing-statedata-attributes-tempzone.json");
        JsonNode interrogationTempZoneNode = mapper.readValue(interrogationTempZoneJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.INTERROGATION_TEMP_ZONE, interrogationTempZoneNode);
        assertThat(errors).hasSize(3);
        assertRequiredProperty(errors.get(0), "/stateData", "date");
        assertRequiredProperty(errors.get(1), "/stateData", "state");
        assertRequiredProperty(errors.get(2), "/stateData", "currentPage");
    }

    @Test
    @DisplayName("when validating interrogation temp zone with incorrect types, return json schema errors")
    void testTempZone04() throws IOException {
        String interrogationTempZoneJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/interrogationtempzone/invalid-types.json");
        JsonNode interrogationTempZoneNode = mapper.readValue(interrogationTempZoneJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.INTERROGATION_TEMP_ZONE, interrogationTempZoneNode);
        assertThat(errors).hasSize(7);
        assertBadType(errors.get(0), "/comment");
        assertBadType(errors.get(1), "/questionnaireId");
        assertBadType(errors.get(2), "/stateData/date");
        assertBadEnum(errors.get(3), "/stateData/state");
        assertBadType(errors.get(4), "/stateData/currentPage");
        assertForbiddenProperty(errors.get(5), "/stateData", "forbidden-property");
        assertForbiddenProperty(errors.get(6), "", "forbidden-property");
    }

    @Test
    @DisplayName("when validating interrogation temp zone with incorrect attributes length, return json schema errors")
    void testTempZone05() throws IOException {
        String interrogationTempZoneJson = JsonTestHelper.getResourceFileAsString("json-schema-validation/interrogationtempzone/invalid-lengths.json");
        JsonNode surveyUnitTempZoneNode = mapper.readValue(interrogationTempZoneJson, JsonNode.class);
        List<Error> errors = validatorComponent.validate(SchemaType.INTERROGATION_TEMP_ZONE, surveyUnitTempZoneNode);
        assertThat(errors).hasSize(2);
        assertBadLength(errors.get(0), "/questionnaireId");
        assertBadLength(errors.get(1), "/stateData/currentPage");
    }

    private void assertBadType(Error error, String instanceLocation) {
        assertThat(error.getInstanceLocation()).hasToString(instanceLocation);
        assertThat(error.getKeyword()).isEqualTo("type");
    }

    private void assertForbiddenProperty(Error error, String instanceLocation, String forbiddenProperty) {
        assertThat(error.getInstanceLocation()).hasToString(instanceLocation);
        assertThat(error.getProperty()).isEqualTo(forbiddenProperty);
        assertThat(error.getKeyword()).isEqualTo("additionalProperties");
    }

    private void assertRequiredProperty(Error error, String instanceLocation, String requiredProperty) {
        assertThat(error.getInstanceLocation()).hasToString(instanceLocation);
        assertThat(error.getProperty()).isEqualTo(requiredProperty);
        assertThat(error.getKeyword()).isEqualTo("required");
    }

    private void assertBadEnum(Error error, String instanceLocation) {
        assertThat(error.getInstanceLocation()).hasToString(instanceLocation);
        assertThat(error.getKeyword()).isEqualTo("enum");
    }

    private void assertBadLength(Error error, String instanceLocation) {
        assertThat(error.getInstanceLocation()).hasToString(instanceLocation);
        assertThat(error.getKeyword()).isEqualTo("minLength");
    }
}
