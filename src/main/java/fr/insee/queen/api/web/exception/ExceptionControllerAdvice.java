package fr.insee.queen.api.web.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.api.campaign.service.exception.CampaignDeletionException;
import fr.insee.queen.api.campaign.service.exception.CampaignServiceException;
import fr.insee.queen.api.campaign.service.exception.QuestionnaireModelServiceException;
import fr.insee.queen.api.depositproof.service.exception.DepositProofException;
import fr.insee.queen.api.integration.controller.component.exception.IntegrationComponentException;
import fr.insee.queen.api.pilotage.service.exception.HabilitationException;
import fr.insee.queen.api.pilotage.service.exception.PilotageApiException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Handle Errors better
@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(ApiRuntimeBaseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public void runtimeException(ApiRuntimeBaseException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public void noHandlerFoundException(NoHandlerFoundException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.NOT_FOUND,
                new ApiBaseException(e.getMessage(), ErrorCode.NO_HANDLER_FOUND));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public void accessDeniedException(AccessDeniedException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.FORBIDDEN,
                new ApiBaseException(e.getMessage(), ErrorCode.FORBIDDEN));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> responseObject = new HashMap<>();
        responseObject.put("code", ErrorCode.BAD_REQUEST.getValue());

        List<String> messages = new ArrayList<>();
        List<String> fields = new ArrayList<>();

        for (ObjectError bindingError : e.getBindingResult().getGlobalErrors()) {
            messages.add(bindingError.getDefaultMessage());
        }

        for (FieldError bindingError : e.getBindingResult().getFieldErrors()) {
            String fieldError = bindingError.getField() + ": " + bindingError.getDefaultMessage();
            fields.add(fieldError);
        }

        if (!messages.isEmpty()) {
            responseObject.put("messages", messages);
        }

        if (!fields.isEmpty()) {
            responseObject.put("fields", fields);
        }
        responseObject.put("status", "error");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseObject));
        response.getWriter().flush();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public void handleConstraintViolation(
            jakarta.validation.ConstraintViolationException e,
            HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);

        List<String> violations = new ArrayList<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String violationMessage = violation.getPropertyPath().toString() + ": " + violation.getMessage();
            violations.add(violationMessage);
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        Map<String, Object> responseObject = new HashMap<>();
        responseObject.put("code", ErrorCode.BAD_REQUEST.getValue());

        if (!violations.isEmpty()) {
            responseObject.put("messages", violations);
        }
        responseObject.put("status", "error");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseObject));
        response.getWriter().flush();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletResponse response) throws IOException {
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
        writeResponse(response, HttpStatus.BAD_REQUEST,
                new ApiBaseException(errorMessage, ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public void noEntityFoundException(EntityNotFoundException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.NOT_FOUND,
                new ApiBaseException(e.getMessage(), ErrorCode.OBJECT_NOT_FOUND));
    }

    @ExceptionHandler(AuthenticationTokenException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public void authenticationTokenExceptionException(AuthenticationTokenException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.INTERNAL_SERVER_ERROR,
                new ApiBaseException("An error occurred", ErrorCode.SERVER_EXCEPTION));
    }

    @ExceptionHandler(HabilitationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public void habilitationException(HabilitationException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.FORBIDDEN,
                new ApiBaseException(e.getMessage(), ErrorCode.FORBIDDEN));
    }

    @ExceptionHandler(CampaignServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void campaignCreationException(CampaignServiceException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.BAD_REQUEST,
                new ApiBaseException(e.getMessage(), ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(CampaignDeletionException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public void campaignDeletionException(CampaignDeletionException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.UNPROCESSABLE_ENTITY,
                new ApiBaseException(e.getMessage(), ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(QuestionnaireModelServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void questionnaireModelServiceException(QuestionnaireModelServiceException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.BAD_REQUEST,
                new ApiBaseException(e.getMessage(), ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(IntegrationComponentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void depositProofException(IntegrationComponentException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.BAD_REQUEST,
                new ApiBaseException(e.getMessage(), ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(PilotageApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void pilotageApiException(PilotageApiException e, HttpServletResponse response) throws IOException {
        writeResponse(response, HttpStatus.BAD_REQUEST,
                new ApiBaseException(e.getMessage(), ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(DepositProofException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public void depositProofException(DepositProofException e, HttpServletResponse response) throws IOException {
        writeResponse(response, HttpStatus.INTERNAL_SERVER_ERROR,
                new ApiBaseException(e.getMessage(), ErrorCode.SERVER_EXCEPTION));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public void exceptions(HttpClientErrorException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.valueOf(e.getStatusCode().value()),
                new ApiBaseException("An error has occurred", ErrorCode.SERVER_EXCEPTION));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public void exceptions(Exception e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.INTERNAL_SERVER_ERROR,
                new ApiBaseException("An error has occurred", ErrorCode.SERVER_EXCEPTION));
    }

    private void writeResponse(HttpServletResponse response, HttpStatus status, BaseException e) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(e.getMapForResponse()));
        response.getWriter().flush();
    }
}