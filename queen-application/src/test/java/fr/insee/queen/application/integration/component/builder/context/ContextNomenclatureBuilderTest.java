package fr.insee.queen.application.integration.component.builder.context;

import fr.insee.queen.application.integration.service.dummy.IntegrationFakeService;
import fr.insee.modelefiliere.CollectionInstrumentDto;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.integration.service.dummy.RegistreFakeService;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import fr.insee.queen.domain.registre.model.CodeList;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ContextNomenclatureBuilderTest {

    private RegistreFakeService registreFakeService;
    private IntegrationFakeService integrationFakeService;
    private ContextNomenclatureBuilder contextNomenclatureBuilder;

    @BeforeEach
    void setUp() {
        registreFakeService = new RegistreFakeService();
        integrationFakeService = new IntegrationFakeService();
        contextNomenclatureBuilder = new ContextNomenclatureBuilder(registreFakeService, integrationFakeService);
    }

    @Test
    @DisplayName("When building nomenclatures with valid collection instrument, then returns success results")
    void test_build_with_valid_collection_instrument_returns_success_results() {
        // Given
        CollectionInstrumentDto collectionInstrumentDto = new CollectionInstrumentDto();
        collectionInstrumentDto.setUrl("http://example.com/collection-instruments/019ce7e8-5250-7afa-b609-c62403fc1fa2");
        collectionInstrumentDto.setCodesListsUrl("http://example.com/collection-instruments/019ce7e8-5250-7afa-b609-c62403fc1fa2/codes-lists");

        CodeList codeList = new CodeList("123e4567-e89b-12d3-a456-426614174000", "http://example.com/codes-lists/123e4567-e89b-12d3-a456-426614174000");
        CodeList codeList2 = new CodeList("123e4567-e89b-12d3-a456-426614174001", "http://example.com/codes-lists/123e4567-e89b-12d3-a456-426614174001");

        ArrayNode modalities = JsonNodeFactory.instance.arrayNode();
        modalities.add("MODALITY_1");
        modalities.add("MODALITY_2");

        ArrayNode modalities2 = JsonNodeFactory.instance.arrayNode();
        modalities2.add("MODALITY_3");
        modalities2.add("MODALITY_4");

        Map<CodeList, ArrayNode> modalitiesMap = new HashMap<>();
        modalitiesMap.put(codeList, modalities);
        modalitiesMap.put(codeList2, modalities2);

        registreFakeService.setModalities(modalitiesMap);

        // When
        List<IntegrationResultUnitDto> results = contextNomenclatureBuilder.build(collectionInstrumentDto);

        // Then
        assertThat(results)
                .isNotNull()
                .hasSize(2);
        assertThat(results.getFirst().getStatus()).isEqualTo(IntegrationStatus.CREATED);
        assertThat(results.get(1).getStatus()).isEqualTo(IntegrationStatus.CREATED);
        assertThat(results).extracting(IntegrationResultUnitDto::getId)
                .containsExactlyInAnyOrder(
                        "123e4567-e89b-12d3-a456-426614174000",
                        "123e4567-e89b-12d3-a456-426614174001");
    }

    @Test
    @DisplayName("When building nomenclatures with no code lists found, then returns error result")
    void test_build_with_no_code_lists_returns_error_result() {
        // Given
        CollectionInstrumentDto collectionInstrumentDto = new CollectionInstrumentDto();
        collectionInstrumentDto.setId("019ce7e8-5250-7afa-b609-c62403fc1fa2");
        collectionInstrumentDto.setUrl("http://example.com/collection-instruments/019ce7e8-5250-7afa-b609-c62403fc1fa2");
        collectionInstrumentDto.setCodesListsUrl("http://example.com/collection-instruments/019ce7e8-5250-7afa-b609-c62403fc1fa2/codes-lists");

        // Configure registre to return empty map (no code lists found)
        registreFakeService.setModalities(Map.of());

        // When
        List<IntegrationResultUnitDto> results = contextNomenclatureBuilder.build(collectionInstrumentDto);

        // Then
        assertThat(results)
                .isNotNull()
                .hasSize(1);
        assertThat(results.getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(results.getFirst().getId()).isEqualTo("019ce7e8-5250-7afa-b609-c62403fc1fa2");
        assertThat(results.getFirst().getCause()).contains("No code lists found in registre for this collection instrument");
    }

    @Test
    @DisplayName("When creating nomenclatures with null modalities, then returns error result")
    void test_create_nomenclatures_with_null_modalities_returns_error_result() {
        // Given
        CollectionInstrumentDto collectionInstrumentDto = new CollectionInstrumentDto();
        collectionInstrumentDto.setUrl("http://example.com/collection-instruments/019ce7e8-5250-7afa-b609-c62403fc1fa2");
        collectionInstrumentDto.setCodesListsUrl("http://example.com/collection-instruments/019ce7e8-5250-7afa-b609-c62403fc1fa2/codes-lists");

        CodeList codeList = new CodeList("123e4567-e89b-12d3-a456-426614174000", "http://example.com/codes-lists/123e4567-e89b-12d3-a456-426614174000");

        // Configure registre to return null modalities for the code list
        Map<CodeList, ArrayNode> modalitiesMap = new HashMap<>();
        modalitiesMap.put(codeList, null);

        registreFakeService.setModalities(modalitiesMap);

        // When
        List<IntegrationResultUnitDto> results = contextNomenclatureBuilder.build(collectionInstrumentDto);

        // Then
        assertThat(results)
                .isNotNull()
                .hasSize(1);
        assertThat(results.getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(results.getFirst().getId()).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
        assertThat(results.getFirst().getCause()).contains("Modalities not found in registre for this codeList");
    }

    @Test
    @DisplayName("When integration service fails to create nomenclature, then returns error result")
    void test_create_nomenclatures_with_invalid_data_returns_error_result() {
        // Given
        CollectionInstrumentDto collectionInstrumentDto = new CollectionInstrumentDto();
        collectionInstrumentDto.setUrl("http://example.com/collection-instruments/019ce7e8-5250-7afa-b609-c62403fc1fa2");
        collectionInstrumentDto.setCodesListsUrl("http://example.com/collection-instruments/019ce7e8-5250-7afa-b609-c62403fc1fa2/codes-lists");

        CodeList codeList = new CodeList("123e4567-e89b-12d3-a456-426614174000", "http://example.com/codes-lists/123e4567-e89b-12d3-a456-426614174000");

        ArrayNode modalities = JsonNodeFactory.instance.arrayNode();
        modalities.add("MODALITY_1");
        modalities.add("MODALITY_2");

        Map<CodeList, ArrayNode> modalitiesMap = new HashMap<>();
        modalitiesMap.put(codeList, modalities);

        registreFakeService.setModalities(modalitiesMap);

        // Configure integration service to fail
        integrationFakeService.setShouldFail(true);

        // When
        List<IntegrationResultUnitDto> results = contextNomenclatureBuilder.build(collectionInstrumentDto);

        // Then
        assertThat(results)
                .isNotNull()
                .hasSize(1);
        assertThat(results.getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(results.getFirst().getId()).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
        assertThat(results.getFirst().getCause()).contains("Failed to create nomenclature");
    }
}