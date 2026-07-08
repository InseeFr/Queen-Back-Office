package fr.insee.queen.application.configuration;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.json.JsonMapper;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class Jackson3JsonFormatMapperTest {

    /**
     * Simple DTO used for round-trip JSON tests.
     */
    record Person(String name, int age) {}

    /**
     * Empty bean used to trigger a serialization error depending on Jackson configuration.
     * If your Jackson config allows empty beans, this test may need to be adapted/removed.
     */
    static class EmptyBean {
        // intentionally empty
    }

    @Test
    @DisplayName("supportsSourceType should support JsonParser and reject unrelated types")
    void supportsSourceType_shouldSupportJsonParser() {
        Jackson3JsonFormatMapper mapper =
                new Jackson3JsonFormatMapper(JsonMapper.builderWithJackson2Defaults().build());

        assertThat(mapper.supportsSourceType(JsonParser.class)).isTrue();
        assertThat(mapper.supportsSourceType(String.class)).isFalse();
    }

    @Test
    @DisplayName("supportsTargetType should support JsonGenerator and reject unrelated types")
    void supportsTargetType_shouldSupportJsonGenerator() {
        Jackson3JsonFormatMapper mapper =
                new Jackson3JsonFormatMapper(JsonMapper.builderWithJackson2Defaults().build());

        assertThat(mapper.supportsTargetType(JsonGenerator.class)).isTrue();
        assertThat(mapper.supportsTargetType(String.class)).isFalse();
    }

    @Test
    @DisplayName("toString/fromString should round-trip a simple object")
    void toStringAndFromString_shouldRoundTrip() {
        JsonMapper jsonMapper = JsonMapper.builderWithJackson2Defaults().build();
        Jackson3JsonFormatMapper mapper = new Jackson3JsonFormatMapper(jsonMapper);

        Person original = new Person("Alice", 42);

        // Serialize
        String json = mapper.toString(original, Person.class);
        assertThat(json).isEqualTo("{\"name\":\"Alice\",\"age\":42}");

        // Deserialize
        Person restored = mapper.fromString(json, Person.class);
        assertThat(restored).isEqualTo(original);
    }

    @Test
    @DisplayName("fromString should wrap JacksonException into IllegalArgumentException on invalid JSON")
    void fromString_invalidJson_shouldThrowIllegalArgumentException() {
        JsonMapper jsonMapper = JsonMapper.builderWithJackson2Defaults().build();
        Jackson3JsonFormatMapper mapper = new Jackson3JsonFormatMapper(jsonMapper);

        assertThatThrownBy(() -> mapper.fromString("{invalid json}", Person.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Could not deserialize string")
                .hasCauseInstanceOf(JacksonException.class);
    }

    @Test
    @DisplayName("toString should wrap JacksonException into IllegalArgumentException on serialization failure")
    void toString_unserializableObject_shouldThrowIllegalArgumentException() {
        JsonMapper jsonMapper = JsonMapper.builderWithJackson2Defaults().build();
        Jackson3JsonFormatMapper mapper = new Jackson3JsonFormatMapper(jsonMapper);

        EmptyBean bean = new EmptyBean();

        assertThatThrownBy(() -> mapper.toString(bean, EmptyBean.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Could not serialize object")
                .hasCauseInstanceOf(JacksonException.class);
    }

    @Test
    @DisplayName("writeToTarget should write JSON to the provided JsonGenerator")
    void writeToTarget_shouldWriteJson() throws Exception {
        JsonMapper jsonMapper = JsonMapper.builderWithJackson2Defaults().build();
        Jackson3JsonFormatMapper mapper = new Jackson3JsonFormatMapper(jsonMapper);

        // Mock Hibernate JavaType and WrapperOptions
        @SuppressWarnings("unchecked")
        JavaType<Person> javaType = Mockito.mock(JavaType.class);
        WrapperOptions options = Mockito.mock(WrapperOptions.class);

        when(javaType.getJavaType()).thenReturn(Person.class);

        StringWriter writer = new StringWriter();
        JsonGenerator generator = jsonMapper.createGenerator(writer);

        mapper.writeToTarget(new Person("Bob", 7), javaType, generator, options);
        generator.flush();

        assertThat(writer.toString()).hasToString("{\"name\":\"Bob\",\"age\":7}");
    }

    @Test
    @DisplayName("readFromSource should read JSON from the provided JsonParser")
    void readFromSource_shouldReadJson() throws Exception {
        JsonMapper jsonMapper = JsonMapper.builderWithJackson2Defaults().build();
        Jackson3JsonFormatMapper mapper = new Jackson3JsonFormatMapper(jsonMapper);

        @SuppressWarnings("unchecked")
        JavaType<Person> javaType = Mockito.mock(JavaType.class);
        WrapperOptions options = Mockito.mock(WrapperOptions.class);

        when(javaType.getJavaType()).thenReturn(Person.class);

        JsonParser parser = jsonMapper.createParser("{\"name\":\"Charlie\",\"age\":33}");

        Person result = mapper.readFromSource(javaType, parser, options);

        assertThat(result).isEqualTo(new Person("Charlie", 33));
    }
}
