package fr.insee.queen.jms.service.utils;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.jms.exception.PropertyException;

/**
 * This utility is used to retrieve mandatory string property from a json object
 * if the property is not found, an exception is raised
 */
public class PropertyValidator {


    public static final String PROPERTY_NOT_EMPTY =
            "Property %s should not be empty";

    /**
     * @param node the node
     * @param field the key for node
     * @throws PropertyException exception raised when property is invalid
     */
    public static String textValue(JsonNode node, String field) throws PropertyException {
        JsonNode n = node.get(field);
        if (n == null || n.isNull() || n.asText().equals("null")) {
            throw new PropertyException("Missing or null field : '" + field + "'");
        }
        if (!n.isTextual()) {
            throw new PropertyException("The field '" + field + "' must be a string (type found : " + n.getNodeType() + ")");
        }
        return n.asText();
    }
}
