package fr.insee.queen.api.web.exception;

import java.util.Map;

public class ApiRuntimeBaseException extends RuntimeException implements BaseException {

    protected final ErrorCode code;
    private final ExceptionResponse response;

    public ApiRuntimeBaseException(String message, ErrorCode code) {
        super(message);

        this.code = code;
        this.response = new ExceptionResponse(code.getValue(), getMessage());
    }

    public Map<String, Object> getMapForResponse() {
        return response.getMapForResponse();
    }
}