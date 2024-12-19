package fr.insee.queen.infrastructure.db.data.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class ObjectNodeConverter implements AttributeConverter<ObjectNode, String> {

    public static final String OBJECTNODE_TO_STRING_ERROR_MESSAGE = "Error during objectnode to string conversion";
    public static final String STRING_TO_OBJECTNODE_ERROR_MESSAGE = "Error during string to objectnode conversion";

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ObjectNode attribute) {
        if(attribute == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException(OBJECTNODE_TO_STRING_ERROR_MESSAGE, e);
        }
    }

    @Override
    public ObjectNode convertToEntityAttribute(String dbData) {
        if(dbData == null) {
            return null;
        }

        try {
            return (ObjectNode) mapper.readTree(dbData);
        } catch (Exception e) {
            throw new IllegalArgumentException(STRING_TO_OBJECTNODE_ERROR_MESSAGE, e);
        }
    }
}
