package fr.insee.queen.application.configuration.auth;

import fr.insee.queen.application.configuration.log.InvalidTokenUserExtractor;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomAuthenticationEntryPointTest {

    private InvalidTokenUserExtractor tokenUserExtractor;
    private LogInterceptor logInterceptor;
    private JsonMapper mapper;
    private CustomAuthenticationEntryPoint entryPoint;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() throws IOException {
        tokenUserExtractor = mock(InvalidTokenUserExtractor.class);
        logInterceptor = mock(LogInterceptor.class);
        mapper = mock(JsonMapper.class);

        entryPoint = new CustomAuthenticationEntryPoint(tokenUserExtractor, logInterceptor, mapper);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/api/foo");
        when(response.getOutputStream()).thenReturn(new NoopServletOutputStream());
    }

    @Test
    void commence_withBearerToken_usesExtractorAndWritesProblem() throws Exception {
        // given
        String rawToken = "header.payload.signature";
        BearerTokenAuthenticationToken bearerAuth = new BearerTokenAuthenticationToken(rawToken);
        AuthenticationException ex = new TestAuthException("Bad token", bearerAuth);

        when(tokenUserExtractor.extractUserId(rawToken)).thenReturn("bob");

        // when
        entryPoint.commence(request, response, ex);

        // then
        verify(tokenUserExtractor).extractUserId(rawToken);
        verify(logInterceptor).injectLogContext(request, "bob");
        verify(logInterceptor).clearLogContext();

        verify(response).setStatus(401);
        verify(response).setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        ArgumentCaptor<ProblemDetail> pdCaptor = ArgumentCaptor.forClass(ProblemDetail.class);
        verify(mapper).writeValue(any(OutputStream.class), pdCaptor.capture());

        ProblemDetail pd = pdCaptor.getValue();
        assertThat(pd.getTitle()).isEqualTo("Unauthorized");
        assertThat(pd.getDetail()).isEqualTo("Unauthorized access");
        assertThat(pd.getInstance()).isEqualTo(URI.create("/api/foo"));
    }

    @Test
    void commence_withNonBearerAuth_usesAuthName() throws Exception {
        // given
        Authentication normalAuth = mock(Authentication.class);
        when(normalAuth.getName()).thenReturn("john");
        AuthenticationException ex = new TestAuthException("Auth failed", normalAuth);

        // when
        entryPoint.commence(request, response, ex);

        // then
        verify(tokenUserExtractor, never()).extractUserId(anyString());
        verify(logInterceptor).injectLogContext(request, "john");
        verify(logInterceptor).clearLogContext();
        verify(response).setStatus(401);
        verify(response).setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        verify(mapper).writeValue(any(OutputStream.class), any(ProblemDetail.class));
    }

    @Test
    void commence_withNullAuth_usesAnonymous() throws Exception {
        // given
        AuthenticationException ex = new TestAuthException("No auth", null);

        // when
        entryPoint.commence(request, response, ex);

        // then
        verify(tokenUserExtractor, never()).extractUserId(anyString());
        verify(logInterceptor).injectLogContext(request, "ANONYMOUSUSER");
        verify(logInterceptor).clearLogContext();
        verify(response).setStatus(401);
        verify(response).setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        verify(mapper).writeValue(any(OutputStream.class), any(ProblemDetail.class));
    }

    // --- Helpers -----------------------------------------------------------------

    /** Exception concrète pour injecter un Authentication dans le test. */
    static class TestAuthException extends AuthenticationException {
        private final Authentication auth;

        public TestAuthException(String msg, Authentication auth) {
            super(msg);
            this.auth = auth;
        }

        /** Méthode utilisée par la classe à tester. */
        @Override
        public Authentication getAuthenticationRequest() {
            return auth;
        }
    }

    /** OutputStream neutre pour stubber HttpServletResponse.getOutputStream(). */
    static class NoopServletOutputStream extends ServletOutputStream {
        @Override public boolean isReady() { return true; }
        @Override public void setWriteListener(WriteListener writeListener) { /* no-op */ }
        @Override public void write(int b) { /* no-op */ }
    }
}

