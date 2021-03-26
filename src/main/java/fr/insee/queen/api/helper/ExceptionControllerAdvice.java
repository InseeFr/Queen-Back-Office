package fr.insee.queen.api.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.api.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ApiRuntimeBaseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public void runtimeException(ApiRuntimeBaseException e, HttpServletResponse response) throws IOException {
        writeResponse(response, HttpServletResponse.SC_NOT_FOUND, e);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public void noHandlerFoundException(NoHandlerFoundException e, HttpServletResponse response) throws IOException {
        writeResponse(response, HttpServletResponse.SC_NOT_FOUND,
                new ApiBaseException(e.getMessage(), ErrorCode.NO_HANDLER_FOUND));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public void exceptions(Exception e, HttpServletResponse response) throws IOException {
        writeResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                new ApiBaseException(e.getMessage(), ErrorCode.SERVER_EXCEPTION));
    }

    private void writeResponse(HttpServletResponse response, int code, BaseException e) throws IOException {
        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(e.getMapForResponse()));
        response.getWriter().flush();
    }
}