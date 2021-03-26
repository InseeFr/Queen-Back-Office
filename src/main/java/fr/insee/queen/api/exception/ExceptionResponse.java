package fr.insee.queen.api.exception;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ExceptionResponse implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5976819193104348L;
	private final int code;
    private final String message;

    public ExceptionResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Map<String, Object> getMapForResponse() {
        Map<String, Object> retVal = new HashMap<>();

        retVal.put("status", "error");
        retVal.put("code", code);
        retVal.put("message", message);

        return retVal;
    }
}