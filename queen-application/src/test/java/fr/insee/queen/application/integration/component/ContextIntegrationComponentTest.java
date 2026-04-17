package fr.insee.queen.application.integration.component;

import fr.insee.queen.application.integration.component.builder.context.RegistreUrlValidator;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.integration.dto.output.IntegrationResultsDto;
import fr.insee.queen.application.integration.service.dummy.ContextNomenclatureFakeBuilder;
import fr.insee.queen.application.integration.service.dummy.ContextQuestionnaireModelFakeBuilder;
import fr.insee.modelefiliere.CollectionInstrumentDto;
import fr.insee.modelefiliere.ContextDto;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContextIntegrationComponentTest {

    private ContextQuestionnaireModelFakeBuilder questionnaireModelFakeBuilder;
    private ContextNomenclatureFakeBuilder nomenclatureFakeBuilder;
    private ContextIntegrationComponent contextIntegrationComponent;

    @BeforeEach
    void setUp() {
        questionnaireModelFakeBuilder = new ContextQuestionnaireModelFakeBuilder();
        nomenclatureFakeBuilder = new ContextNomenclatureFakeBuilder();
        RegistreUrlValidator registreUrlValidator = new RegistreUrlValidator("http://example.com");
        contextIntegrationComponent = new ContextIntegrationComponent(
                questionnaireModelFakeBuilder,
                nomenclatureFakeBuilder,
                registreUrlValidator
        );
    }

    @Test
    @DisplayName("When processing context with valid collection instruments, then returns aggregated results from both builders")
    void test_process_context_with_valid_instruments_returns_aggregated_results() {
        // Given
        ContextDto contextDto = createTestContextDto();

        // Configure fake builders to return success results
        questionnaireModelFakeBuilder.setResults(List.of(
                IntegrationResultUnitDto.integrationResultUnitCreated("qm-1")
        ));
        nomenclatureFakeBuilder.setResults(List.of(
                IntegrationResultUnitDto.integrationResultUnitCreated("nom-1")
        ));

        // When
        IntegrationResultsDto result = contextIntegrationComponent.processContext(contextDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getQuestionnaireModels())
                .isNotNull()
                .hasSize(1);
        assertThat(result.getQuestionnaireModels().getFirst().getId()).isEqualTo("qm-1");
        assertThat(result.getQuestionnaireModels().getFirst().getStatus()).isEqualTo(IntegrationStatus.CREATED);

        assertThat(result.getNomenclatures())
                .isNotNull()
                .hasSize(1);
        assertThat(result.getNomenclatures().getFirst().getId()).isEqualTo("nom-1");
        assertThat(result.getNomenclatures().getFirst().getStatus()).isEqualTo(IntegrationStatus.CREATED);
    }

    @Test
    @DisplayName("When questionnaire model builder fails, then returns error result for questionnaire models")
    void test_process_context_with_questionnaire_builder_failure() {
        // Given
        ContextDto contextDto = createTestContextDto();

        // Configure questionnaire builder to fail
        questionnaireModelFakeBuilder.setShouldFail(true);
        questionnaireModelFakeBuilder.setErrorMessage("Questionnaire model creation failed");

        // Configure nomenclature builder to succeed
        nomenclatureFakeBuilder.setResults(List.of(
                IntegrationResultUnitDto.integrationResultUnitCreated("nom-1")
        ));

        // When
        IntegrationResultsDto result = contextIntegrationComponent.processContext(contextDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getQuestionnaireModels())
                .isNotNull()
                .hasSize(1);
        assertThat(result.getQuestionnaireModels().getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(result.getQuestionnaireModels().getFirst().getCause()).contains("Questionnaire model creation failed");

        assertThat(result.getNomenclatures())
                .isNotNull()
                .hasSize(1);
        assertThat(result.getNomenclatures().getFirst().getStatus()).isEqualTo(IntegrationStatus.CREATED);
    }

    @Test
    @DisplayName("When nomenclature builder fails, then returns error result for nomenclatures")
    void test_process_context_with_nomenclature_builder_failure() {
        // Given
        ContextDto contextDto = createTestContextDto();

        // Configure questionnaire builder to succeed
        questionnaireModelFakeBuilder.setResults(List.of(
                IntegrationResultUnitDto.integrationResultUnitCreated("qm-1")
        ));

        // Configure nomenclature builder to fail
        nomenclatureFakeBuilder.setShouldFail(true);
        nomenclatureFakeBuilder.setErrorMessage("Nomenclature creation failed");

        // When
        IntegrationResultsDto result = contextIntegrationComponent.processContext(contextDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getQuestionnaireModels())
                .isNotNull()
                .hasSize(1);
        assertThat(result.getQuestionnaireModels().getFirst().getStatus()).isEqualTo(IntegrationStatus.CREATED);

        assertThat(result.getNomenclatures())
                .isNotNull()
                .hasSize(1);
        assertThat(result.getNomenclatures().getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(result.getNomenclatures().getFirst().getCause()).contains("Nomenclature creation failed");
    }

    @Test
    @DisplayName("When both builders fail, then returns error results for both")
    void test_process_context_with_both_builders_failing() {
        // Given
        ContextDto contextDto = createTestContextDto();

        // Configure both builders to fail
        questionnaireModelFakeBuilder.setShouldFail(true);
        nomenclatureFakeBuilder.setShouldFail(true);

        // When
        IntegrationResultsDto result = contextIntegrationComponent.processContext(contextDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getQuestionnaireModels())
                .isNotNull()
                .hasSize(1);
        assertThat(result.getQuestionnaireModels().getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);

        assertThat(result.getNomenclatures())
                .isNotNull()
                .hasSize(1);
        assertThat(result.getNomenclatures().getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
    }

    @Test
    @DisplayName("When processing context with empty collection instruments, then returns empty results")
    void test_process_context_with_empty_instruments() {
        // Given
        ContextDto contextDto = new ContextDto();
        contextDto.setId(UUID.randomUUID());
        contextDto.setCollectionInstruments(new ArrayList<>());

        // When
        IntegrationResultsDto result = contextIntegrationComponent.processContext(contextDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getQuestionnaireModels())
                .isNotNull()
                .isEmpty();
        assertThat(result.getNomenclatures())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("When collection instrument has invalid url, then throws IllegalArgumentException")
    void test_process_context_with_invalid_url_throws_exception() {
        // Given
        ContextDto contextDto = new ContextDto();
        contextDto.setId(UUID.randomUUID());

        String id = UUID.randomUUID().toString();
        CollectionInstrumentDto instrumentDto = new CollectionInstrumentDto();
        instrumentDto.setId(id);
        instrumentDto.setUrl("http://invalid-domain.com/collection-instruments/" + id);
        instrumentDto.setCodesListsUrl("http://example.com/collection-instruments/" + id + "/codes-lists");

        contextDto.setCollectionInstruments(List.of(instrumentDto));

        // When / Then
        assertThatThrownBy(() -> contextIntegrationComponent.processContext(contextDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("URL invalide");
    }

    @Test
    @DisplayName("When collection instrument has invalid codesListsUrl, then throws IllegalArgumentException")
    void test_process_context_with_invalid_codes_lists_url_throws_exception() {
        // Given
        ContextDto contextDto = new ContextDto();
        contextDto.setId(UUID.randomUUID());

        String id = UUID.randomUUID().toString();
        CollectionInstrumentDto instrumentDto = new CollectionInstrumentDto();
        instrumentDto.setId(id);
        instrumentDto.setUrl("http://example.com/collection-instruments/" + id);
        instrumentDto.setCodesListsUrl("http://invalid-domain.com/collection-instruments/" + id + "/codes-lists");

        contextDto.setCollectionInstruments(List.of(instrumentDto));

        // When / Then
        assertThatThrownBy(() -> contextIntegrationComponent.processContext(contextDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CodesListsUrl invalide");
    }

    // Helper methods

    private ContextDto createTestContextDto() {
        ContextDto contextDto = new ContextDto();
        contextDto.setId(UUID.randomUUID());

        String id = UUID.randomUUID().toString();
        CollectionInstrumentDto instrumentDto = new CollectionInstrumentDto();
        instrumentDto.setId(id);
        instrumentDto.setUrl("http://example.com/collection-instruments/" + id);
        instrumentDto.setCodesListsUrl("http://example.com/collection-instruments/" + id + "/codes-lists");

        contextDto.setCollectionInstruments(List.of(instrumentDto));
        return contextDto;
    }
}