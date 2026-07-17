package fr.insee.queen.jms.service.utils;

import tools.jackson.databind.JsonNode;
import fr.insee.queen.jms.exception.PropertyException;

/**
 * This utility is used to retrieve mandatory string property from a json object
 * if the property is not found, an exception is raised
 */
public class PropertyValidator {
    private PropertyValidator() {
        throw new  IllegalStateException("Utility class");
    }

    /**
     * @param rootNode the node
     * @param field the key for node
     * @throws PropertyException exception raised when property is invalid
     */
    public static String textValue(JsonNode rootNode, String field) throws PropertyException {
        JsonNode fieldNode = rootNode.get(field);
        if (fieldNode == null || fieldNode.isNull() || fieldNode.asString().equals("null")) {
            throw new PropertyException("Missing or null field : '" + field + "'");
        }

        if (!fieldNode.isString()) {
            throw new PropertyException("The field '" + field + "' must be a string (type found : " + fieldNode.getNodeType() + ")");
        }
        String fieldValue = fieldNode.asString();
        if(fieldValue.isBlank()) {
            throw new PropertyException("The field '" + field + "' must not be empty");
        }
        return fieldNode.asString();
    }
}
