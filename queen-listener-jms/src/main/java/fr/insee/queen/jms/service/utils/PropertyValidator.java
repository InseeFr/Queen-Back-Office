package fr.insee.queen.jms.service.utils;

import fr.insee.queen.jms.exception.PropertyException;

import java.util.UUID;

/**
 * This utility is used to retrieve mandatory string property from a json object
 * if the property is not found, an exception is raised
 */
public class PropertyValidator {

    private PropertyValidator() {
        throw new IllegalArgumentException("Utility class");
    }

    public static final String PROPERTY_NOT_EMPTY =
            "Property %s should not be empty";

    /**
     * @param propertyName the property name
     * @param property the property to check
     * @throws PropertyException exception raised when property is invalid
     * value is not a string
     */
    public static void checkPropertyValue(String propertyName, String property) throws PropertyException {
        if(property == null || property.isBlank()) {
            throw new PropertyException(
                    String.format(PROPERTY_NOT_EMPTY, propertyName)
            );
        }
    }

    /**
     * @param propertyName the property name
     * @param property the property to check
     * @throws PropertyException exception raised when property is invalid
     * value is not a string
     */
    public static void checkPropertyValue(String propertyName, UUID property) throws PropertyException {
        if(property == null) {
            throw new PropertyException(
                    String.format(PROPERTY_NOT_EMPTY, propertyName)
            );
        }
    }
}
