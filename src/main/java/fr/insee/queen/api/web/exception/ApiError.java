package fr.insee.queen.api.web.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

/**
 * Default API Error object returned as JSON response to client
 */
@Data
@AllArgsConstructor
public class ApiError {
    private Integer code;
    private String path;
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
    private Date timestamp;

    /**
     * @param status http status for this error
     * @param path origin request path
     * @param timestamp timestamp of the generated error
     * @param errorMessage error message
     */
    public ApiError (HttpStatus status, String path, Date timestamp, String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = status.getReasonPhrase();
        }
        createApiError(status.value(), path, timestamp, errorMessage);
    }

    private void createApiError(int code, String path, Date timestamp, String errorMessage) {
        this.code = code;
        this.path = path;
        this.message = errorMessage;
        this.timestamp = timestamp;
    }
}
