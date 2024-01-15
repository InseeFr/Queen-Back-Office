package fr.insee.queen.application.web.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Map;


/**
 * Component used to build APIError objects
 */
@Component
public class ApiExceptionComponent {

    private final ErrorAttributes errorAttributes;

    public ApiExceptionComponent(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /**
     *
     * @param request origin request
     * @param status status from exception
     * @param errorMessage error message
     * @return error object used for JSON response
     */
    public ApiError buildApiErrorObject(WebRequest request, HttpStatus status, String errorMessage) {
        String path = getPath(request);
        Date timestamp = getTimeStamp(request);
        return new ApiError(status, path, timestamp,  errorMessage);
    }

    /**
     * @param request origin request
     * @return get timestamp from error attributes
     */
    private Date getTimeStamp(WebRequest request) {
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        return ((Date) attributes.get("timestamp"));
    }

    /**
     *
     * @param request origin request
     * @return get path from origin request
     */
    private String getPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
