package fr.insee.queen.api.exception;


public enum ErrorCode {

    OBJECT_NOT_FOUND(1),
    OBJECT_IS_INVALID(2),

    NO_HANDLER_FOUND(100),
    SERVER_EXCEPTION(900),
    FORBIDDEN(403),
    BAD_REQUEST(400);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}