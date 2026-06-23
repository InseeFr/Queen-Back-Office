package fr.insee.queen.application.configuration.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class GroupKindTest {

    @ParameterizedTest(name = "{0} → singular={1}, plural={2}")
    @CsvSource({
            "CAMPAIGN, campaign, campaigns",
            "PARTITION, partition, partitions"
    })
    @DisplayName("GroupKind maps kind to correct URL paths")
    void testPathMapping(String kindName, String expectedSingular, String expectedPlural) {
        GroupKind kind = GroupKind.valueOf(kindName);
        assertThat(kind.getPathSingular()).isEqualTo(expectedSingular);
        assertThat(kind.getPathPlural()).isEqualTo(expectedPlural);
    }

    @Test
    @DisplayName("GroupKind has exactly two values")
    void testEnumValues() {
        assertThat(GroupKind.values()).hasSize(2);
    }
}
