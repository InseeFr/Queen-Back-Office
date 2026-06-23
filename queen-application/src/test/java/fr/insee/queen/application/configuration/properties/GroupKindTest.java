package fr.insee.queen.application.configuration.properties;

import fr.insee.queen.domain.group.model.GroupKind;
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
        // Given / When
        GroupKind kind = GroupKind.valueOf(kindName);

        // Then
        assertThat(kind.getPathSingular()).isEqualTo(expectedSingular);
        assertThat(kind.getPathPlural()).isEqualTo(expectedPlural);
    }

    @Test
    @DisplayName("GroupKind has exactly two values")
    void testEnumValues() {
        // Given / When / Then
        assertThat(GroupKind.values()).hasSize(2);
    }

    @Test
    @DisplayName("GroupKind enum names match the strings persisted in the survey_group.kind column")
    void enum_names_stay_in_sync_with_persisted_values() {
        // Given / When
        String campaignName = GroupKind.CAMPAIGN.name();
        String partitionName = GroupKind.PARTITION.name();

        // Then
        assertThat(campaignName).isEqualTo("CAMPAIGN");
        assertThat(partitionName).isEqualTo("PARTITION");
    }
}
