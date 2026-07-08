package fr.insee.queen.application.web.exception;

import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.service.StateDataApiService;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidTransitionException;
import fr.insee.queen.infrastructure.depositproof.exception.DepositProofException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DatabindException;

import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExceptionControllerAdvice")
class ExceptionControllerAdviceTest {

    private final ExceptionControllerAdvice advice = new ExceptionControllerAdvice();

    @Test
    @DisplayName("NoHandlerFoundException → 404 with original message")
    void noHandlerFoundException_returns_404() {
        // Given
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/missing", new HttpHeaders());

        // When
        ProblemDetail pd = advice.noHandlerFoundException(ex);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(pd.getDetail()).isEqualTo(ex.getMessage());
    }

    @Test
    @DisplayName("NoResourceFoundException → 404 with original message")
    void noResourceFoundException_returns_404() {
        // Given
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/missing", "static");

        // When
        ProblemDetail pd = advice.noResourceFoundException(ex);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(pd.getDetail()).isEqualTo(ex.getMessage());
    }

    @Test
    @DisplayName("AccessDeniedException → 403 with original message")
    void accessDeniedException_returns_403() {
        // Given
        AccessDeniedException ex = new AccessDeniedException("Forbidden");

        // When
        ProblemDetail pd = advice.accessDeniedException(ex);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(pd.getDetail()).isEqualTo("Forbidden");
    }

    @Test
    @DisplayName("EntityNotFoundException → 404 with original message")
    void noEntityFoundException_returns_404() {
        // Given
        EntityNotFoundException ex = new EntityNotFoundException("missing");

        // When
        ProblemDetail pd = advice.noEntityFoundException(ex);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(pd.getDetail()).isEqualTo("missing");
    }

    @Test
    @DisplayName("DepositProofException → 500")
    void depositProofException_returns_500() {
        // Given
        DepositProofException ex = new DepositProofException();

        // When
        ProblemDetail pd = advice.depositProofException(ex);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(pd.getDetail()).isEqualTo(ex.getMessage());
    }

    @Test
    @DisplayName("MethodArgumentNotValidException → 400 \"Invalid parameters\"")
    void methodArgumentNotValid_returns_400_with_invalid_parameters() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        // When
        ProblemDetail pd = advice.handleMethodArgumentNotValid(ex, null);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(pd.getDetail()).isEqualTo("Invalid parameters");
    }

    @Test
    @DisplayName("ConstraintViolationException → 400 \"Invalid data\"")
    void constraintViolation_returns_400_with_invalid_data() {
        // Given
        ConstraintViolationException ex = new ConstraintViolationException("violation", Set.of());

        // When
        ProblemDetail pd = advice.handleConstraintViolation(ex, null);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(pd.getDetail()).isEqualTo("Invalid data");
    }

    @Test
    @DisplayName("RestClientException → 500 \"An error has occurred\"")
    void restClientException_returns_500_with_generic_message() {
        // Given
        RestClientException ex = new RestClientException("network down");

        // When
        ProblemDetail pd = advice.exceptions(ex);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(pd.getDetail()).isEqualTo("An error has occurred");
    }

    @Test
    @DisplayName("Generic Exception → 500 \"An error has occurred\"")
    void genericException_returns_500_with_generic_message() {
        // Given
        Exception ex = new Exception("boom");

        // When
        ProblemDetail pd = advice.exceptions(ex);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(pd.getDetail()).isEqualTo("An error has occurred");
    }

    @Test
    @DisplayName("when root cause is missing, returns the default deserialization message")
    void unknown_root_cause_returns_default_message() {
        // Given
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("unreadable");
        when(ex.getRootCause()).thenReturn(null);

        // When
        ProblemDetail pd = advice.handleHttpMessageNotReadableException(ex);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(pd.getDetail()).isEqualTo("Error when deserializing JSON");
    }

    @Test
    @DisplayName("when root cause is a StreamReadException, mentions JSON syntax and reports the location")
    void stream_read_exception_returns_syntax_message_with_location() {
        // Given
        StreamReadException root = mock(StreamReadException.class, RETURNS_DEEP_STUBS);
        when(root.getLocation().getLineNr()).thenReturn(7);
        when(root.getLocation().getColumnNr()).thenReturn(3);
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("unreadable");
        when(ex.getRootCause()).thenReturn(root);

        // When
        ProblemDetail pd = advice.handleHttpMessageNotReadableException(ex);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(pd.getDetail())
                .contains("Error with JSON syntax")
                .contains("[line: 7, column: 3]");
    }

    @Test
    @DisplayName("when root cause is a DatabindException, mentions expected property types and reports the location")
    void databind_exception_returns_mapping_message_with_location() {
        // Given
        DatabindException root = mock(DatabindException.class, RETURNS_DEEP_STUBS);
        when(root.getLocation().getLineNr()).thenReturn(4);
        when(root.getLocation().getColumnNr()).thenReturn(12);
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("unreadable");
        when(ex.getRootCause()).thenReturn(root);

        // When
        ProblemDetail pd = advice.handleHttpMessageNotReadableException(ex);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(pd.getDetail())
                .contains("Error when deserializing JSON")
                .contains("expected types")
                .contains("[line: 4, column: 12]");
    }

    @Test
    @DisplayName("On StateDataInvalidTransitionException, return HTTP 409 CONFLICT with the ApiError built by errorComponent")
    void should_return_conflict_when_state_data_invalid_transition() {
        // Given
        StateDataInvalidTransitionException ex = new StateDataInvalidTransitionException("Invalid transition");

        // When
        ProblemDetail pd = advice.stateDataInvalidTransitionExceptionException(ex);

        // Then
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(pd.getDetail()).isEqualTo(StateDataApiService.INVALID_TRANSITION_MESSAGE);

    }
}


