package fr.insee.queen.api.web.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import fr.insee.queen.api.campaign.service.exception.CampaignDeletionException;
import fr.insee.queen.api.campaign.service.exception.QuestionnaireInvalidException;
import fr.insee.queen.api.depositproof.service.exception.DepositProofException;
import fr.insee.queen.api.integration.controller.component.exception.IntegrationComponentException;
import fr.insee.queen.api.pilotage.service.exception.HabilitationException;
import fr.insee.queen.api.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.api.surveyunit.service.exception.StateDataDateInvalidDateException;
import fr.insee.queen.api.web.authentication.AuthenticationTokenException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Handle API exceptions for project
 * Do not work on exceptions occuring before/outside controllers scope
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionControllerAdvice {

    private final ApiExceptionComponent errorComponent;

    private static final String ERROR_OCCURRED_LABEL = "An error has occurred";

    /**
     * Global method to process the catched exception
     *
     * @param ex      Exception catched
     * @param status  status linked with this exception
     * @param request request initiating the exception
     * @return the apierror object with linked status code
     */
    private ResponseEntity<ApiError> generateResponseError(Exception ex, HttpStatus status, WebRequest request) {
        return generateResponseError(ex, status, request, null);
    }

    /**
     * Global method to process the catched exception
     *
     * @param ex                   Exception catched
     * @param status               status linked with this exception
     * @param request              request initiating the exception
     * @param overrideErrorMessage message overriding default error message from exception
     * @return the apierror object with linked status code
     */
    private ResponseEntity<ApiError> generateResponseError(Exception ex, HttpStatus status, WebRequest request, String overrideErrorMessage) {
        String errorMessage = ex.getMessage();
        if (overrideErrorMessage != null) {
            errorMessage = overrideErrorMessage;
        }
        ApiError error = errorComponent.buildApiErrorObject(request, status, errorMessage);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> noHandlerFoundException(NoHandlerFoundException e, WebRequest request) {
        log.error(e.getMessage());
        return generateResponseError(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> accessDeniedException(AccessDeniedException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            WebRequest request) {
        log.error(e.getMessage(), e);
        return generateResponseError(e, HttpStatus.BAD_REQUEST, request, "Invalid parameters");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException e,
            WebRequest request) {
        log.error(e.getMessage(), e);
        return generateResponseError(e, HttpStatus.BAD_REQUEST, request, "Invalid data");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, WebRequest request) {
        log.error(e.getMessage(), e);

        Throwable rootCause = e.getRootCause();

        String errorMessage = "Error when deserializing JSON";
        if (rootCause instanceof JsonParseException parseException) {
            String location = parseException.getLocation() != null ? "[line: " + parseException.getLocation().getLineNr() + ", column: " + parseException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error with JSON syntax. Check that your json is well formatted: " + parseException.getOriginalMessage() + " " + location;
        }
        if (rootCause instanceof JsonMappingException mappingException) {
            String location = mappingException.getLocation() != null ? "[line: " + mappingException.getLocation().getLineNr() + ", column: " + mappingException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error when deserializing JSON. Check that your JSON properties are of the expected types " + location;
        }
        return generateResponseError(e, HttpStatus.BAD_REQUEST, request, errorMessage);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> noEntityFoundException(EntityNotFoundException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return generateResponseError(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AuthenticationTokenException.class)
    public ResponseEntity<ApiError> authenticationTokenExceptionException(AuthenticationTokenException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, request, ERROR_OCCURRED_LABEL);
    }

    @ExceptionHandler(HabilitationException.class)
    public ResponseEntity<ApiError> habilitationException(HabilitationException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return generateResponseError(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(QuestionnaireInvalidException.class)
    public ResponseEntity<ApiError> questionnaireInvalidException(QuestionnaireInvalidException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return generateResponseError(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(CampaignDeletionException.class)
    public ResponseEntity<ApiError> campaignDeletionException(CampaignDeletionException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return generateResponseError(e, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<ApiError> entityAlreadyExistException(EntityAlreadyExistException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return generateResponseError(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(IntegrationComponentException.class)
    public ResponseEntity<ApiError> integrationComponentException(IntegrationComponentException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return generateResponseError(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(PilotageApiException.class)
    public ResponseEntity<ApiError> pilotageApiException(PilotageApiException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(DepositProofException.class)
    public ResponseEntity<ApiError> depositProofException(DepositProofException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(StateDataDateInvalidDateException.class)
    public ResponseEntity<ApiError> stateDataException(StateDataDateInvalidDateException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiError> exceptions(RestClientException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, request, ERROR_OCCURRED_LABEL);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> exceptions(Exception e, WebRequest request) {
        log.error(e.getMessage(), e);
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, request, ERROR_OCCURRED_LABEL);
    }
}