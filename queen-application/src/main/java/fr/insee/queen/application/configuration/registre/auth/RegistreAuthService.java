package fr.insee.queen.application.configuration.registre.auth;

import fr.insee.queen.domain.registre.service.exception.RegistreAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "feature.registre.enabled", havingValue = "true")
public class RegistreAuthService {
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final ClientRegistrationRepository clientRegistrationRepository;


    public String getAccessToken() {
        try {
            ClientRegistration registration = clientRegistrationRepository
                    .findByRegistrationId("registre-service-account");

            OAuth2AuthorizedClient authorizedClient = authorizedClientManager
                    .authorize(OAuth2AuthorizeRequest
                            .withClientRegistrationId(registration.getRegistrationId())
                            .principal("registre-service-account")
                            .build());

            if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
                throw new RegistreAuthException("Failed to obtain access token for registre service account");
            }

            return authorizedClient.getAccessToken().getTokenValue();
        } catch (RegistreAuthException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RegistreAuthException("Error retrieving access token for registre service account", ex);
        }
    }
}