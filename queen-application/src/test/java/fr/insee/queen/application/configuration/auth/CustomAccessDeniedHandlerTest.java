package fr.insee.queen.application.configuration.auth;

import fr.insee.queen.application.configuration.log.LogInterceptor;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomAccessDeniedHandlerTest {

    private JsonMapper mapper;
    private LogInterceptor logInterceptor;
    private CustomAccessDeniedHandler handler;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() throws IOException {
        mapper = mock(JsonMapper.class);
        logInterceptor = mock(LogInterceptor.class);
        handler = new CustomAccessDeniedHandler(mapper, logInterceptor);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/secure/resource");
        when(response.getOutputStream()).thenReturn(new NoopServletOutputStream());
    }

    @Test
    void handle_shouldLogAndReturnProblemDetail() throws Exception {
        // given
        AccessDeniedException ex = new AccessDeniedException("nope");

        // when
        handler.handle(request, response, ex);

        // then
        verify(logInterceptor).injectLogContext(request);
        verify(logInterceptor).clearLogContext();

        verify(response).setStatus(403);
        verify(response).setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        ArgumentCaptor<ProblemDetail> captor = ArgumentCaptor.forClass(ProblemDetail.class);
        verify(mapper).writeValue(any(OutputStream.class), captor.capture());

        ProblemDetail pd = captor.getValue();
        assertThat(pd).isNotNull();
        assertThat(pd.getTitle()).isEqualTo("Forbidden");
        assertThat(pd.getDetail()).isEqualTo("Access denied");
        assertThat(pd.getInstance()).isEqualTo(URI.create("/secure/resource"));
    }

    // --- Helpers ---------------------------------------------------------------

    /** OutputStream neutre pour stubber HttpServletResponse.getOutputStream(). */
    static class NoopServletOutputStream extends ServletOutputStream {
        @Override public boolean isReady() { return true; }
        @Override public void setWriteListener(WriteListener writeListener) { /* no-op */ }
        @Override public void write(int b) { /* no-op */ }
    }
}

