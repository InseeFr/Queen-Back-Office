package fr.insee.queen.application.web.exception;

import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidTransitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionControllerAdviceTest {

    @Mock
    private ApiExceptionComponent errorComponent;

    @Mock
    private WebRequest request;

    @InjectMocks
    private ExceptionControllerAdvice exceptionControllerAdvice;

    @Test
    @DisplayName("On StateDataInvalidTransitionException, return HTTP 409 CONFLICT with the ApiError built by errorComponent")
    void should_return_conflict_when_state_data_invalid_transition() {
        // given
        String errorMessage = "invalid transition";
        StateDataInvalidTransitionException exception = new StateDataInvalidTransitionException(errorMessage);
        ApiError expectedError = new ApiError(HttpStatus.CONFLICT, "/api/path", new Date(), errorMessage);
        when(errorComponent.buildApiErrorObject(any(WebRequest.class), eq(HttpStatus.CONFLICT), eq(errorMessage)))
                .thenReturn(expectedError);

        // when
        ResponseEntity<ApiError> response = exceptionControllerAdvice
                .stateDataInvalidTransitionExceptionException(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo(expectedError);
    }
}
