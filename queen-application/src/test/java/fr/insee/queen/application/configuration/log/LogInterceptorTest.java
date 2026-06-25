package fr.insee.queen.application.configuration.log;

import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogInterceptorTest {

    @Mock
    private AuthenticationHelper authenticationHelper;

    @Mock
    private HttpServletRequest request;

    private LogInterceptor logInterceptor;

    @BeforeEach
    void setUp() {
        logInterceptor = new LogInterceptor(authenticationHelper);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/foo");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("When a user id is provided, it is upper-cased in the MDC")
    void injectLogContext_with_user_id_uppercases_it_in_mdc() {
        // Given / When
        logInterceptor.injectLogContext(request, "bob");

        // Then
        assertThat(MDC.get("user")).isEqualTo("BOB");
        assertThat(MDC.get("method")).isEqualTo("GET");
        assertThat(MDC.get("path")).isEqualTo("/api/foo");
        assertThat(MDC.get("id")).isNotBlank();
    }

    @Test
    @DisplayName("When the user id is null, the MDC falls back to ANONYMOUSUSER")
    void injectLogContext_with_null_user_id_falls_back_to_anonymous() {
        // Given / When
        logInterceptor.injectLogContext(request, null);

        // Then
        assertThat(MDC.get("user")).isEqualTo("ANONYMOUSUSER");
    }

    @Test
    @DisplayName("When the security context exposes a principal, its name lands upper-cased in the MDC")
    void injectLogContext_reads_user_id_from_security_context(@Mock Authentication authentication) {
        // Given
        when(authenticationHelper.getAuthenticationPrincipal()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("alice");

        // When
        logInterceptor.injectLogContext(request);

        // Then
        assertThat(MDC.get("user")).isEqualTo("ALICE");
    }

    @Test
    @DisplayName("When no principal is bound to the security context, the MDC falls back to ANONYMOUSUSER")
    void injectLogContext_uses_anonymous_when_security_context_has_no_principal() {
        // Given
        when(authenticationHelper.getAuthenticationPrincipal()).thenReturn(null);

        // When
        logInterceptor.injectLogContext(request);

        // Then
        assertThat(MDC.get("user")).isEqualTo("ANONYMOUSUSER");
    }

    @Test
    @DisplayName("clearLogContext removes all entries previously injected into the MDC")
    void clearLogContext_clears_all_mdc_entries() {
        // Given
        logInterceptor.injectLogContext(request, "bob");
        assertThat(MDC.get("user")).isNotNull();

        // When
        logInterceptor.clearLogContext();

        // Then
        assertThat(MDC.get("user")).isNull();
        assertThat(MDC.get("id")).isNull();
        assertThat(MDC.get("path")).isNull();
        assertThat(MDC.get("method")).isNull();
    }
}
