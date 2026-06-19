package fr.insee.queen.application.configuration.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.boot.SpringApplication;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class GroupPathEnvironmentPostProcessorTest {

    private final GroupPathEnvironmentPostProcessor processor = new GroupPathEnvironmentPostProcessor();
    private final SpringApplication application = new SpringApplication();

    @ParameterizedTest(name = "kind={0} → singular={1}, plural={2}")
    @CsvSource({
            "CAMPAIGN, campaign, campaigns",
            "PARTITION, partition, partitions",
            "campaign, campaign, campaigns",
            "partition, partition, partitions"
    })
    @DisplayName("derives path-singular and path-plural from kind")
    void testDerivedPaths(String kind, String expectedSingular, String expectedPlural) {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("application.group.kind", kind);

        processor.postProcessEnvironment(env, application);

        assertThat(env.getProperty("application.group.path-singular")).isEqualTo(expectedSingular);
        assertThat(env.getProperty("application.group.path-plural")).isEqualTo(expectedPlural);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("defaults to CAMPAIGN paths when kind is null or empty")
    void testDefaultsToCAMPAIGN(String kind) {
        MockEnvironment env = new MockEnvironment();
        if (kind != null) {
            env.setProperty("application.group.kind", kind);
        }

        processor.postProcessEnvironment(env, application);

        assertThat(env.getProperty("application.group.path-singular")).isEqualTo("campaign");
        assertThat(env.getProperty("application.group.path-plural")).isEqualTo("campaigns");
    }

    @Test
    @DisplayName("defaults to CAMPAIGN paths when kind is unknown value")
    void testDefaultsToCAMPAIGNForUnknownKind() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("application.group.kind", "UNKNOWN_VALUE");

        processor.postProcessEnvironment(env, application);

        assertThat(env.getProperty("application.group.path-singular")).isEqualTo("campaign");
        assertThat(env.getProperty("application.group.path-plural")).isEqualTo("campaigns");
    }
}
