package fr.insee.queen.api.controller.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.api.exception.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

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

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public void noEntityFoundException(EntityNotFoundException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.NOT_FOUND,
                new ApiBaseException(e.getMessage(), ErrorCode.OBJECT_NOT_FOUND));
    }

    @ExceptionHandler(HabilitationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public void habilitationException(HabilitationException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.FORBIDDEN,
                new ApiBaseException(e.getMessage(), ErrorCode.FORBIDDEN));
    }

    @ExceptionHandler(CampaignCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void campaignCreationException(CampaignCreationException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.BAD_REQUEST,
                new ApiBaseException(e.getMessage(), ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(CampaignDeletionException.class)
    @ResponseBody
    public void campaignDeletionException (CampaignDeletionException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.UNPROCESSABLE_ENTITY,
                new ApiBaseException(e.getMessage(), ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(QuestionnaireModelCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void campaignCreationException(QuestionnaireModelCreationException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        writeResponse(response, HttpStatus.BAD_REQUEST,
                new ApiBaseException(e.getMessage(), ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(SurveyUnitCreateUpdateRepositoryException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public void campaignCreationException(SurveyUnitCreateUpdateRepositoryException e, HttpServletResponse response) throws IOException {
        writeResponse(response, HttpStatus.INTERNAL_SERVER_ERROR,
                new ApiBaseException(e.getMessage(), ErrorCode.SERVER_EXCEPTION));
    }

    @ExceptionHandler(IntegrationServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void depositProofException(IntegrationServiceException e, HttpServletResponse response) throws IOException {
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
                new ApiBaseException(e.getMessage(), ErrorCode.SERVER_EXCEPTION));
    }

    private void writeResponse(HttpServletResponse response, HttpStatus status, BaseException e) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(e.getMapForResponse()));
        response.getWriter().flush();
    }
}