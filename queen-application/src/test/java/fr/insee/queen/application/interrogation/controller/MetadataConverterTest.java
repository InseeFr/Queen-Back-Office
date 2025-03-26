package fr.insee.queen.application.interrogation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.campaign.component.MetadataComponentConverter;
import fr.insee.queen.application.interrogation.dto.output.*;
import fr.insee.queen.domain.interrogation.service.exception.MetadataValueNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MetadataConverterTest {
    private MetadataComponentConverter metadataConverter;
    private static final String LABEL_SUFFIX = "_label";

    @BeforeEach
    void init() {
        ObjectMapper mapper = new ObjectMapper();
        metadataConverter = new MetadataComponentConverter(mapper);
    }

    @ParameterizedTest
    @MethodSource("generateInvalidMetadata")
    @DisplayName("when retrieving interrogation metadata throw error when metadata data is invalid")
    void testRetrieveMetaData01(ObjectNode invalidMetadata, String missingKey) {
        assertThatThrownBy(() -> metadataConverter.convert(invalidMetadata))
                .isInstanceOf(MetadataValueNotFoundException.class)
                .hasMessage(String.format(MetadataValueNotFoundException.ERROR_MESSAGE, missingKey));
    }

    @Test
    @DisplayName("Retrieve interrogation metadata without mandatory variables in variables list")
    void testRetrieveMetaData02() {
        String otherKey = "other-attribute";
        ObjectNode metadata = generateMetadata(QuestionnaireContextDto.HOUSEHOLD,
                List.of(MetadataComponentConverter.METADATA_LABEL, MetadataComponentConverter.METADATA_OBJECTIVES, otherKey));

        List<MetadataVariableDto> variables = new ArrayList<>();
        variables.add(new MetadataVariableDto(otherKey, otherKey + LABEL_SUFFIX));
        MetadataDto metadataExpected = new MetadataDto(variables, null,
                QuestionnaireContextDto.HOUSEHOLD, MetadataComponentConverter.METADATA_LABEL + LABEL_SUFFIX,
                MetadataComponentConverter.METADATA_OBJECTIVES + LABEL_SUFFIX);

        assertThat(metadataConverter.convert(metadata)).isEqualTo(metadataExpected);
    }

    @Test
    @DisplayName("Retrieve interrogation metadata with logos")
    void testRetrieveMetaData03() {
        ObjectNode metadata = generateMetadata(QuestionnaireContextDto.HOUSEHOLD,
                List.of(MetadataComponentConverter.METADATA_LABEL, MetadataComponentConverter.METADATA_OBJECTIVES));

        ArrayNode logoNodes = JsonNodeFactory.instance.arrayNode();
        LogoDto mainLogo = null;
        List<LogoDto> secondaryLogos = new ArrayList<>();
        List<String> logoUrls = List.of("logo1","logo2","logo3");
        for(int cpt = 0; cpt < logoUrls.size(); cpt ++) {
            String logoUrl = logoUrls.get(cpt);
            ObjectNode logoNode = JsonNodeFactory.instance.objectNode();
            logoNode.put(MetadataComponentConverter.METADATA_LOGO_URL, logoUrl);
            logoNode.put(MetadataComponentConverter.METADATA_LOGO_LABEL, logoUrl + LABEL_SUFFIX);
            logoNodes.add(logoNode);
            LogoDto logoDto = new LogoDto(logoUrl, logoUrl + LABEL_SUFFIX);
            if(cpt == 0) {
                mainLogo = logoDto;
                continue;
            }
            secondaryLogos.add(logoDto);
        }

        LogoDtos logoDtos = new LogoDtos(mainLogo, secondaryLogos);
        metadata.set(MetadataComponentConverter.METADATA_LOGOS, logoNodes);

        List<MetadataVariableDto> variables = new ArrayList<>();

        MetadataDto metadataExpected = new MetadataDto(variables, logoDtos,
                QuestionnaireContextDto.HOUSEHOLD, MetadataComponentConverter.METADATA_LABEL + LABEL_SUFFIX,
                MetadataComponentConverter.METADATA_OBJECTIVES + LABEL_SUFFIX);

        assertThat(metadataConverter.convert(metadata)).isEqualTo(metadataExpected);
    }

    private static Stream<Arguments> generateInvalidMetadata() {
        ObjectNode invalidIntegerVariableMetadata = JsonNodeFactory.instance.objectNode();
        invalidIntegerVariableMetadata.put(MetadataComponentConverter.METADATA_CONTEXT, QuestionnaireContextDto.HOUSEHOLD.getLabel());

        ArrayNode variables = JsonNodeFactory.instance.arrayNode();
        ObjectNode variable = JsonNodeFactory.instance.objectNode();
        variable.put(MetadataComponentConverter.METADATA_VARIABLE_NAME, MetadataComponentConverter.METADATA_LABEL);
        variable.put(MetadataComponentConverter.METADATA_VARIABLE_VALUE, "label");
        variables.add(variable);
        variable = JsonNodeFactory.instance.objectNode();
        variable.put(MetadataComponentConverter.METADATA_VARIABLE_NAME, MetadataComponentConverter.METADATA_OBJECTIVES);
        variable.put(MetadataComponentConverter.METADATA_VARIABLE_VALUE, 12);
        invalidIntegerVariableMetadata.set(MetadataComponentConverter.METADATA_VARIABLES, variables);

        return Stream.of(
                Arguments.of(invalidIntegerVariableMetadata,
                        MetadataComponentConverter.METADATA_OBJECTIVES),
                Arguments.of(generateMetadata(
                                null, List.of(MetadataComponentConverter.METADATA_LABEL, MetadataComponentConverter.METADATA_OBJECTIVES)),
                        MetadataComponentConverter.METADATA_CONTEXT),
                Arguments.of(generateMetadata(QuestionnaireContextDto.BUSINESS, List.of(MetadataComponentConverter.METADATA_OBJECTIVES)),
                        MetadataComponentConverter.METADATA_LABEL),
                Arguments.of(generateMetadata(QuestionnaireContextDto.HOUSEHOLD, List.of(MetadataComponentConverter.METADATA_LABEL)),
                        MetadataComponentConverter.METADATA_OBJECTIVES),
                Arguments.of(generateMetadata(QuestionnaireContextDto.HOUSEHOLD, new ArrayList<>()),
                        MetadataComponentConverter.METADATA_VARIABLES)
        );
    }

    private static ObjectNode generateMetadata(QuestionnaireContextDto context, List<String> metadataKeys) {
        ObjectNode metadata = JsonNodeFactory.instance.objectNode();
        if(context != null) {
            metadata.put(MetadataComponentConverter.METADATA_CONTEXT, context.getLabel());
        }
        ArrayNode variables = JsonNodeFactory.instance.arrayNode();

        for(String metadataKey : metadataKeys) {
            ObjectNode variable = JsonNodeFactory.instance.objectNode();
            variable.put(MetadataComponentConverter.METADATA_VARIABLE_NAME, metadataKey);
            variable.put(MetadataComponentConverter.METADATA_VARIABLE_VALUE, metadataKey + LABEL_SUFFIX);
            variables.add(variable);
        }

        if(!metadataKeys.isEmpty()) {
            metadata.set(MetadataComponentConverter.METADATA_VARIABLES, variables);
        }
        return metadata;
    }
}
