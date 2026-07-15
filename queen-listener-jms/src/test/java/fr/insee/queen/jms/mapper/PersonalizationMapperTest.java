package fr.insee.queen.jms.mapper;

import fr.insee.modelefiliere.CoverPageDataDto;
import fr.insee.modelefiliere.InterrogationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonalizationMapperTest {

    private final JsonMapper jsonMapper = new JsonMapper();
    private PersonalizationMapper personalizationMapper;

    @Mock
    private InterrogationDto interrogation;

    @Mock
    private CoverPageDataDto cover;

    @BeforeEach
    void setUp() {
        personalizationMapper = new PersonalizationMapper(jsonMapper);
    }

    @Test
    @DisplayName("Returns an empty array when cover page data is missing")
    void returnsEmptyArrayWhenCoverIsNull() {
        // Given
        when(interrogation.getExtCoverPageData()).thenReturn(null);

        // When
        ArrayNode result = personalizationMapper.toArrayNode(interrogation);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Maps all whoAnswers fields when all are present")
    void mapsAllWhoAnswersWhenAllPresent() {
        // Given
        when(interrogation.getExtCoverPageData()).thenReturn(cover);
        when(cover.getWhoAnswers1()).thenReturn("Manager");
        when(cover.getWhoAnswers2()).thenReturn("Accounting department");
        when(cover.getWhoAnswers3()).thenReturn("Within 10 days");

        // When
        ArrayNode result = personalizationMapper.toArrayNode(interrogation);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).get("name").asString()).isEqualTo("whoAnswers1");
        assertThat(result.get(0).get("value").asString()).isEqualTo("Manager");
        assertThat(result.get(1).get("name").asString()).isEqualTo("whoAnswers2");
        assertThat(result.get(1).get("value").asString()).isEqualTo("Accounting department");
        assertThat(result.get(2).get("name").asString()).isEqualTo("whoAnswers3");
        assertThat(result.get(2).get("value").asString()).isEqualTo("Within 10 days");
    }

    @Test
    @DisplayName("Skips null whoAnswers entries")
    void skipsNullEntries() {
        // Given
        when(interrogation.getExtCoverPageData()).thenReturn(cover);
        when(cover.getWhoAnswers1()).thenReturn("Manager");
        when(cover.getWhoAnswers2()).thenReturn(null);
        when(cover.getWhoAnswers3()).thenReturn("Within 10 days");

        // When
        ArrayNode result = personalizationMapper.toArrayNode(interrogation);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).get("name").asString()).isEqualTo("whoAnswers1");
        assertThat(result.get(1).get("name").asString()).isEqualTo("whoAnswers3");
    }

    @Test
    @DisplayName("Returns an empty array when every whoAnswers is null")
    void returnsEmptyArrayWhenAllNull() {
        // Given
        when(interrogation.getExtCoverPageData()).thenReturn(cover);
        when(cover.getWhoAnswers1()).thenReturn(null);
        when(cover.getWhoAnswers2()).thenReturn(null);
        when(cover.getWhoAnswers3()).thenReturn(null);

        // When
        ArrayNode result = personalizationMapper.toArrayNode(interrogation);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Keeps empty strings as valid values")
    void keepsEmptyStringValues() {
        // Given
        when(interrogation.getExtCoverPageData()).thenReturn(cover);
        when(cover.getWhoAnswers1()).thenReturn("");
        when(cover.getWhoAnswers2()).thenReturn(null);
        when(cover.getWhoAnswers3()).thenReturn(null);

        // When
        ArrayNode result = personalizationMapper.toArrayNode(interrogation);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("value").asString()).isEmpty();
    }
}
