package fr.insee.queen.jms.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseCode {
    CREATED(201),
    OK(200),
    BUSINESS_ERROR(422),
    TECHNICAL_ERROR(500);

    @JsonValue
    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
