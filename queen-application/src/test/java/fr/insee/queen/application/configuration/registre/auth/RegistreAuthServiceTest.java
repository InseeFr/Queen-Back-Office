package fr.insee.queen.application.configuration.registre.auth;

import fr.insee.queen.domain.registre.service.exception.RegistreAuthException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistreAuthServiceTest {

    @Mock
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @InjectMocks
    private RegistreAuthService registreAuthService;

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private ClientRegistration buildClientRegistration() {
        return ClientRegistration
                .withRegistrationId("registre-service-account")
                .clientId("client-id")
                .clientSecret("client-secret")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri("http://localhost/token")
                .build();
    }

    private OAuth2AccessToken buildAccessToken(String tokenValue) {
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                tokenValue,
                Instant.now(),
                Instant.now().plusSeconds(3600));
    }

    // ------------------------------------------------------------------
    // Nominal case
    // ------------------------------------------------------------------

    @Test
    void getAccessToken_shouldReturnTokenValue_whenAuthorizedClientIsValid() {
        ClientRegistration registration = buildClientRegistration();
        OAuth2AccessToken accessToken = buildAccessToken("valid-token-value");
        OAuth2AuthorizedClient authorizedClient =
                new OAuth2AuthorizedClient(registration, "registre-service-account", accessToken);

        when(clientRegistrationRepository.findByRegistrationId("registre-service-account"))
                .thenReturn(registration);
        when(authorizedClientManager.authorize(any())).thenReturn(authorizedClient);

        String token = registreAuthService.getAccessToken();

        assertThat(token).isEqualTo("valid-token-value");
        verify(authorizedClientManager, times(1)).authorize(any());
    }

    // ------------------------------------------------------------------
    // authorizedClient is null
    // ------------------------------------------------------------------

    @Test
    void getAccessToken_shouldThrowRegistreAuthException_whenAuthorizedClientIsNull() {
        ClientRegistration registration = buildClientRegistration();

        when(clientRegistrationRepository.findByRegistrationId("registre-service-account"))
                .thenReturn(registration);
        when(authorizedClientManager.authorize(any())).thenReturn(null);

        assertThatThrownBy(() -> registreAuthService.getAccessToken())
                .isInstanceOf(RegistreAuthException.class)
                .hasMessageContaining("Failed to obtain access token for registre service account");
    }

    // ------------------------------------------------------------------
    // accessToken is null inside the authorized client
    // ------------------------------------------------------------------

    @Test
    void getAccessToken_shouldThrowRegistreAuthException_whenAccessTokenIsNull() {
        ClientRegistration registration = buildClientRegistration();

        OAuth2AuthorizedClient authorizedClient = mock(OAuth2AuthorizedClient.class);
        when(authorizedClient.getAccessToken()).thenReturn(null);

        when(clientRegistrationRepository.findByRegistrationId("registre-service-account"))
                .thenReturn(registration);
        when(authorizedClientManager.authorize(any())).thenReturn(authorizedClient);

        assertThatThrownBy(() -> registreAuthService.getAccessToken())
                .isInstanceOf(RegistreAuthException.class)
                .hasMessageContaining("Failed to obtain access token for registre service account");
    }

    // ------------------------------------------------------------------
    // Unexpected exception is wrapped
    // ------------------------------------------------------------------

    @Test
    void getAccessToken_shouldWrapUnexpectedException_inRegistreAuthException() {
        when(clientRegistrationRepository.findByRegistrationId(any()))
                .thenThrow(new RuntimeException("connection refused"));

        assertThatThrownBy(() -> registreAuthService.getAccessToken())
                .isInstanceOf(RegistreAuthException.class)
                .hasMessageContaining("Error retrieving access token for registre service account")
                .hasCauseInstanceOf(RuntimeException.class)
                .getCause()
                .hasMessage("connection refused");
    }

    // ------------------------------------------------------------------
    // RegistreAuthException is re-thrown as-is (not double-wrapped)
    // ------------------------------------------------------------------

    @Test
    void getAccessToken_shouldRethrowRegistreAuthException_withoutWrapping() {
        RegistreAuthException original = new RegistreAuthException("already a RegistreAuthException");

        when(clientRegistrationRepository.findByRegistrationId(any()))
                .thenThrow(original);

        // The catch (RegistreAuthException ex) { throw ex; } block re-throws the original
        assertThatThrownBy(() -> registreAuthService.getAccessToken())
                .isSameAs(original);
    }
}