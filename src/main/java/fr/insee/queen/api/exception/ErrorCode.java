package fr.insee.queen.api.exception;


public enum ErrorCode {

    OBJECT_NOT_FOUND(1),
    OBJECT_IS_INVALID(2),

    NO_HANDLER_FOUND(100),
    SERVER_EXCEPTION(900);

    private final int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}