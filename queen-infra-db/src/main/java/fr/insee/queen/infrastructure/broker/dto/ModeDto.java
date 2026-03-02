package fr.insee.queen.infrastructure.broker.dto;


import com.fasterxml.jackson.annotation.JsonValue;


import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Collect modes
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-24T14:11:23.623660900+01:00[Europe/Paris]", comments = "Generator version: 7.9.0")
public enum ModeDto {

    CATI("CATI"),

    CAPI("CAPI"),

    CAWI("CAWI"),

    PAPI("PAPI");

    private String value;

    ModeDto(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static ModeDto fromValue(String value) {
        for (ModeDto b : ModeDto.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}


