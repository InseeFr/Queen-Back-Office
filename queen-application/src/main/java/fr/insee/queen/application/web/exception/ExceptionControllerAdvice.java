package fr.insee.queen.application.web.exception;

import fr.insee.queen.application.integration.component.exception.IntegrationComponentException;
import fr.insee.queen.application.web.authentication.AuthenticationTokenException;
import fr.insee.queen.application.web.validation.exception.JsonValidatorComponentInitializationException;
import fr.insee.queen.domain.group.service.exception.GroupDeletionException;
import fr.insee.queen.domain.group.service.exception.GroupNotLinkedToQuestionnaireException;
import fr.insee.queen.domain.group.service.exception.QuestionnaireInvalidException;
import fr.insee.queen.domain.common.exception.EntityAlreadyExistException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidTransitionException;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationAlreadyExistException;
import fr.insee.queen.domain.pilotage.service.exception.HabilitationException;
import fr.insee.queen.domain.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.domain.interrogation.service.exception.MetadataValueNotFoundException;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import fr.insee.queen.infrastructure.db.data.exception.UpdateCollectedDataException;
import fr.insee.queen.infrastructure.depositproof.exception.DepositProofException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DatabindException;

import java.net.URI;

/**
 * Handle API exceptions for project
 * Do not work on exceptions occuring before/outside controllers scope
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionControllerAdvice {
    private static final String ERROR_OCCURRED_LABEL = "An error has occurred";

    private static final String ERROR_INVALID_DATA = "Data is invalid";

    /**
     * Global method to process the catched exception
     *
     * @param ex      Exception catched
     * @param status  status linked with this exception
     * @return the apierror object with linked status code
     */
    private ProblemDetail generateResponseError(Exception ex, HttpStatus status) {
        return generateResponseError(ex, status, null);
    }

    /**
     * Global method to process the catched exception
     *
     * @param ex      Exception catched
     * @param status  status linked with this exception
     * @param shouldGenerateStackTraceLog    should generate stack trace log
     * @return the apierror object with linked status code
     */
    private ProblemDetail generateResponseError(Exception ex, HttpStatus status, boolean shouldGenerateStackTraceLog) {
        return generateResponseError(ex, status, null, null, shouldGenerateStackTraceLog);
    }

    /**
     * Global method to process the catched exception
     *
     * @param ex                   Exception catched
     * @param status               status linked with this exception
     * @param overrideErrorMessage message overriding default error message from exception
     * @return the apierror object with linked status code
     */
    private ProblemDetail generateResponseError(Exception ex, HttpStatus status, String overrideErrorMessage) {
        return generateResponseError(ex, status, null, overrideErrorMessage, true);
    }

    /**
     * Global method to process the catched exception
     *
     * @param ex                   Exception catched
     * @param status               status linked with this exception
     * @param problemType          URI of the problem type
     * @param overrideErrorMessage message overriding default error message from exception
     * @param shouldGenerateStackTraceLog    should generate log
     * @return the ProblemDetail object with linked status code
     */
    private ProblemDetail generateResponseError(Exception ex, HttpStatus status, URI problemType, String overrideErrorMessage, boolean shouldGenerateStackTraceLog) {
        if(shouldGenerateStackTraceLog) {
            log.error(ex.getMessage(), ex);
        } else {
            log.error(ex.getMessage());
        }

        String errorMessage = ex.getMessage();
        if (overrideErrorMessage != null) {
            errorMessage = overrideErrorMessage;
        }
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, errorMessage);

        if(problemType != null) {
            problemDetail.setType(problemType);
        }
        return problemDetail;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ProblemDetail noHandlerFoundException(NoHandlerFoundException e) {
        return generateResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail noResourceFoundException(NoResourceFoundException e) {
        return generateResponseError(e, HttpStatus.NOT_FOUND, false);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail accessDeniedException(AccessDeniedException e) {
        return generateResponseError(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST, "Invalid parameters");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(
            ConstraintViolationException e) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST, "Invalid data");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {

        Throwable rootCause = e.getRootCause();

        String errorMessage = "Error when deserializing JSON";
        if (rootCause instanceof StreamReadException parseException) {
            String location = parseException.getLocation() != null ? "[line: " + parseException.getLocation().getLineNr() + ", column: " + parseException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error with JSON syntax. Check that your json is well formatted: " + location;
        }
        if (rootCause instanceof DatabindException mappingException) {
            String location = mappingException.getLocation() != null ? "[line: " + mappingException.getLocation().getLineNr() + ", column: " + mappingException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error when deserializing JSON. Check that your JSON properties are of the expected types " + location;
        }
        return generateResponseError(e, HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail noEntityFoundException(EntityNotFoundException e) {
        return generateResponseError(e, HttpStatus.NOT_FOUND, false);
    }

    @ExceptionHandler(UpdateCollectedDataException.class)
    public ProblemDetail updateCollectedDataException(UpdateCollectedDataException e) {
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(StateDataInvalidTransitionException.class)
    public ProblemDetail stateDataInvalidTransitionExceptionException(StateDataInvalidTransitionException e) {
        return generateResponseError(e, HttpStatus.CONFLICT, false);
    }

    @ExceptionHandler(AuthenticationTokenException.class)
    public ProblemDetail authenticationTokenExceptionException(AuthenticationTokenException e) {
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, ERROR_OCCURRED_LABEL);
    }

    @ExceptionHandler(HabilitationException.class)
    public ProblemDetail habilitationException(HabilitationException e) {
        return generateResponseError(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(QuestionnaireInvalidException.class)
    public ProblemDetail questionnaireInvalidException(QuestionnaireInvalidException e) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GroupDeletionException.class)
    public ProblemDetail groupDeletionException(GroupDeletionException e) {
        return generateResponseError(e, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @ExceptionHandler(GroupNotLinkedToQuestionnaireException.class)
    public ProblemDetail groupDeletionException(GroupNotLinkedToQuestionnaireException e) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ProblemDetail entityAlreadyExistException(EntityAlreadyExistException e) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InterrogationAlreadyExistException.class)
    public ProblemDetail interrogationAlreadyExistException(InterrogationAlreadyExistException e) {
        return generateResponseError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IntegrationComponentException.class)
    public ProblemDetail integrationComponentException(IntegrationComponentException e) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonValidatorComponentInitializationException.class)
    public ProblemDetail integrationComponentException(JsonValidatorComponentInitializationException e) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST, ERROR_INVALID_DATA);
    }

    @ExceptionHandler(PilotageApiException.class)
    public ProblemDetail pilotageApiException(PilotageApiException e) {
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DepositProofException.class)
    public ProblemDetail depositProofException(DepositProofException e) {
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MetadataValueNotFoundException.class)
    public ProblemDetail metadataValueNotFoundException(MetadataValueNotFoundException e) {
        return generateResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StateDataInvalidDateException.class)
    public ProblemDetail stateDataException(StateDataInvalidDateException e) {
        return generateResponseError(e, HttpStatus.CONFLICT, false);
    }

    @ExceptionHandler(RestClientException.class)
    public ProblemDetail exceptions(RestClientException e) {
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, ERROR_OCCURRED_LABEL);
    }

    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public ProblemDetail asyncRequestNotUsable(AsyncRequestNotUsableException e) {
        // a problem detail is returned but client will not receive the response as the request is not usable anymore
        // client disconnected, broken pipe, ...
        return generateResponseError(e, HttpStatus.CONFLICT, false);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail exceptions(Exception e) {
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, ERROR_OCCURRED_LABEL);
    }
}