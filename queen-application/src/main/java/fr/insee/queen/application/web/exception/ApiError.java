package fr.insee.queen.application.web.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty
    private Integer code;
    @JsonProperty
    private String path;
    @JsonProperty
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
        this.code = status.value();
        this.path = path;
        this.message = errorMessage;
        this.timestamp = timestamp;
    }
}
