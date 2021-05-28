package fr.insee.queen.api.exception;

import java.util.Map;

public class ApiBaseException extends Exception implements BaseException {

    static final long serialVersionUID = 10000L;
    protected final ErrorCode code;
    private final ExceptionResponse response;

    public ApiBaseException(String message, ErrorCode code) {
        super(message);

        this.code = code;
        this.response = new ExceptionResponse(code.getValue(), getMessage());
    }

    public Map<String, Object> getMapForResponse() {
        return response.getMapForResponse();
    }
}