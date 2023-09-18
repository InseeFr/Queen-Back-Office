package fr.insee.queen.api.controller.utils;

import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AuthenticationHelper {
    private ApplicationProperties applicationProperties;

    public String getAuthToken(Authentication auth) {
        if(auth == null || !(auth.getCredentials() instanceof AbstractOAuth2Token token)) {
            return null;
        }
        return token.getTokenValue();
    }

    public String getUserId(Authentication authentication) {
        return switch (applicationProperties.auth()) {
            case BASIC -> {
                Object basic = authentication.getPrincipal();
                if (basic instanceof UserDetails userDetails) {
                    yield userDetails.getUsername();
                }
                yield basic.toString();
            }
            case KEYCLOAK -> {
                if(authentication.getCredentials() instanceof Jwt jwt) {
                    yield jwt.getClaims().get("preferred_username").toString();
                }
                yield Constants.GUEST;
            }
            default -> Constants.GUEST;
        };
    }
}
