package fr.insee.queen.api.controller.utils;

import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.exception.AuthenticationTokenException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AuthenticationHelper {
    private ApplicationProperties applicationProperties;

    public String getAuthToken(Authentication auth) {
        if(auth == null ||!(auth.getCredentials() instanceof AbstractOAuth2Token token)) {
            throw new AuthenticationTokenException("Cannot retrieve token for the user. Ensure you are not in NOAUTH mode with pilotage integration override to false");
        }
        return token.getTokenValue();
    }

    public String getUserId(Authentication authentication) {
        switch(applicationProperties.auth()) {
            case NOAUTH -> {
                return Constants.GUEST;
            }
            case KEYCLOAK -> {
                if(authentication.getCredentials() instanceof Jwt jwt) {
                    return jwt.getClaims().get("preferred_username").toString();
                }
                throw new AuthenticationTokenException("Cannot retrieve token for the user.");
            }
            default -> throw new AuthenticationTokenException("No authentication mode used");
        }
    }
}
