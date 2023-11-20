package fr.insee.queen.api.web.authentication;

import fr.insee.queen.api.configuration.auth.AuthConstants;
import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationUserHelper implements AuthenticationHelper {
    private final ApplicationProperties applicationProperties;

    @Override
    public String getAuthToken(Authentication auth) {
        if (auth == null) {
            throw new AuthenticationTokenException("Cannot retrieve token for the user. Ensure you are not in NOAUTH mode with pilotage integration override to false");
        }
        AbstractOAuth2Token token = (AbstractOAuth2Token) auth.getCredentials();
        return token.getTokenValue();
    }

    @Override
    public String getUserId(Authentication authentication) {
        switch (applicationProperties.auth()) {
            case NOAUTH -> {
                return AuthConstants.GUEST;
            }
            case OIDC -> {
                if (authentication.getCredentials() instanceof Jwt jwt) {
                    return jwt.getClaims().get("preferred_username").toString();
                }
                throw new AuthenticationTokenException("Cannot retrieve token for the user.");
            }
            default -> throw new AuthenticationTokenException("No authentication mode used");
        }
    }
}

