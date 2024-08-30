package fr.insee.queen.jms.service.utils;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.jms.exception.PropertyException;

/**
 * This utility is used to retrieve mandatory string property from a json object
 * if the property is not found, an exception is raised
 */
public class JsonPropertyRetriever {

    private JsonPropertyRetriever() {
        throw new IllegalArgumentException("Utility class");
    }

    public static final String PROPERTY_NOT_TEXTUAL_MESSAGE =
            "Property %s does not have a textual value";

    /**
     *
     * @param sourceNode json source node
     * @param propertyToFind the property to find
     * @return the property value
     * @throws PropertyException exception raised when property is not found or if the propery
     * value is not a string
     */
    public static String getPropertyValue(JsonNode sourceNode, String propertyToFind) throws PropertyException {
        JsonNode propertyValue = sourceNode.path(propertyToFind);

        if(!propertyValue.isTextual()) {
            throw new PropertyException(
                    String.format(PROPERTY_NOT_TEXTUAL_MESSAGE, propertyToFind)
            );
        }
        return propertyValue.asText();
    }
}
