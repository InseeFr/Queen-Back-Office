package fr.insee.queen.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.mock.env.MockEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PropertiesLoggerTest {

    private PropertiesLogger propertiesLogger;

    @BeforeEach
    void setUp() {
        propertiesLogger = new PropertiesLogger();
    }

    @Test
    @DisplayName("Test that properties containing sensitive words are masked")
    void testSensitivePropertiesAreMasked() {
        // given
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("application.password", "supersecret");
        environment.setProperty("spring.token", "123456789");

        // when
        Object maskedPassword = propertiesLogger.hideProperties("application.password", environment);
        Object maskedToken = propertiesLogger.hideProperties("spring.token", environment);

        // then
        assertThat(maskedPassword).isEqualTo("******");
        assertThat(maskedToken).isEqualTo("******");
    }

    @Test
    @DisplayName("Test that non-sensitive properties are not masked")
    void testNonSensitivePropertiesAreNotMasked() {
        // given
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("application.name", "test-app");

        // when
        Object propertyName = propertiesLogger.hideProperties("application.name", environment);

        // then
        assertThat(propertyName).isEqualTo("test-app");
    }

    @Test
    @DisplayName("Test filtering relevant properties")
    void testFilteringRelevantProperties() {
        // given
        Map<String, Object> properties = new HashMap<>();
        properties.put("application.name", "test-app");
        properties.put("spring.active", "production");
        properties.put("unrelated.property", "value");

        AbstractEnvironment environment = new MockEnvironment();
        environment.getPropertySources().addLast(new MapPropertySource("test", properties));

        // when
        Stream<String> relevantProperties = propertiesLogger.getFilteredPropertyStream(environment);

        // then
        var relevantList = relevantProperties.toList();
        assertThat(relevantList)
                .contains("application.name", "spring.active")
                .doesNotContain("unrelated.property");
    }

    @Test
    @DisplayName("Test relevant property detection")
    void testRelevantPropertyDetection() {
        // given
        String relevantProperty = "spring.datasource.url";
        String irrelevantProperty = "unrelated.property";

        // when
        boolean isRelevant = propertiesLogger.isRelevantProperty(relevantProperty);
        boolean isNotRelevant = propertiesLogger.isRelevantProperty(irrelevantProperty);

        // then
        assertThat(isRelevant).isTrue();
        assertThat(isNotRelevant).isFalse();
    }

    @Test
    @DisplayName("Test empty environment")
    void testEmptyEnvironment() {
        // given
        AbstractEnvironment environment = new MockEnvironment();

        // when
        Stream<String> relevantProperties = propertiesLogger.getFilteredPropertyStream(environment);

        // then
        assertThat(relevantProperties.toList()).isEmpty();
    }
}