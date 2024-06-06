package fr.insee.queen.application.campaign.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.surveyunit.dto.output.*;
import fr.insee.queen.domain.surveyunit.service.exception.MetadataValueNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MetadataComponentConverter implements MetadataConverter {
    public static final String METADATA_CONTEXT = "inseeContext";
    public static final String METADATA_OBJECTIVES = "Enq_ObjectifsCourts";
    public static final String METADATA_LABEL = "Enq_LibelleEnquete";
    public static final String METADATA_VARIABLES = "variables";
    public static final String METADATA_VARIABLE_NAME = "name";
    public static final String METADATA_VARIABLE_VALUE = "value";
    public static final String METADATA_LOGOS = "logos";
    public static final String METADATA_LOGO_LABEL = "label";
    public static final String METADATA_LOGO_URL = "url";

    private final ObjectMapper mapper;

    public MetadataDto convert(ObjectNode metadataNode) {
        QuestionnaireContextDto context = extractMetadataContext(metadataNode);

        JsonNode metadataVariablesNode = metadataNode.get(METADATA_VARIABLES);
        if(metadataVariablesNode == null || !metadataVariablesNode.isArray()) {
            throw new MetadataValueNotFoundException(METADATA_VARIABLES);
        }
        String shortObjectives = extractMetadataVariable(metadataVariablesNode, METADATA_OBJECTIVES);
        String shortLabel = extractMetadataVariable(metadataVariablesNode, METADATA_LABEL);
        List<MetadataVariableDto> variables = getVariables((ArrayNode)metadataVariablesNode);
        LogoDtos logos = null;

        JsonNode metadataLogosNode = metadataNode.get(METADATA_LOGOS);
        if(metadataLogosNode != null && metadataLogosNode.isArray()) {
            logos = getLogos((ArrayNode)metadataLogosNode);
        }
        return new MetadataDto(variables, logos, context, shortLabel, shortObjectives);
    }

    private List<MetadataVariableDto> getVariables(ArrayNode variablesNode) {
        List<MetadataVariableDto> variables = new ArrayList<>();
        Iterator<JsonNode> iteratorVariables = variablesNode.elements();
        while(iteratorVariables.hasNext()) {
            JsonNode variableNode = iteratorVariables.next();
            JsonNode nameNode = variableNode.get(METADATA_VARIABLE_NAME);
            JsonNode valueNode = variableNode.get(METADATA_VARIABLE_VALUE);
            variables.add(new MetadataVariableDto(nameNode.textValue(), mapper.convertValue(valueNode, Object.class)));
        }
        return variables;
    }

    private LogoDtos getLogos(ArrayNode metadataLogosNode) {
        LogoDto mainLogo = null;
        List<LogoDto> secondaryLogos = new ArrayList<>();
        Iterator<JsonNode> iteratorMetadataLogosNode = metadataLogosNode.elements();

        while(iteratorMetadataLogosNode.hasNext()) {
            JsonNode variableNode = iteratorMetadataLogosNode.next();
            JsonNode urlNode = variableNode.get(METADATA_LOGO_URL);
            JsonNode labelNode = variableNode.get(METADATA_LOGO_LABEL);
            LogoDto logo = new LogoDto(urlNode.textValue(), labelNode.textValue());
            if(mainLogo == null) {
                mainLogo = logo;
                continue;
            }
            secondaryLogos.add(logo);
        }
        if(mainLogo == null) {
            return null;
        }

        if(secondaryLogos.isEmpty()) {
            return LogoDtos.createWithMainLogoOnly(mainLogo);
        }
        return LogoDtos.create(mainLogo, secondaryLogos);
    }

    private QuestionnaireContextDto extractMetadataContext(ObjectNode metadata) {
        JsonNode metadataValue = metadata.get(METADATA_CONTEXT);
        if(metadataValue == null || !metadataValue.isTextual()) {
            throw new MetadataValueNotFoundException(METADATA_CONTEXT);
        }
        metadata.remove(METADATA_CONTEXT);
        return QuestionnaireContextDto.getQuestionnaireContext(metadataValue.textValue());
    }

    private String extractMetadataVariable(JsonNode metadataVariables, String metadataKey) {
        Iterator<JsonNode> iteratorVariables = metadataVariables.elements();
        while(iteratorVariables.hasNext()) {
            JsonNode variable = iteratorVariables.next();

            if(! variable.isObject()) {
                throw new MetadataValueNotFoundException(metadataKey);
            }
            JsonNode name = variable.get(METADATA_VARIABLE_NAME);
            if(!metadataKey.equals(name.textValue())) {
                continue;
            }

            JsonNode value = variable.get(METADATA_VARIABLE_VALUE);
            if(!value.isTextual()) {
                throw new MetadataValueNotFoundException(metadataKey);
            }
            iteratorVariables.remove();
            return value.textValue();
        }
        throw new MetadataValueNotFoundException(metadataKey);
    }
}
