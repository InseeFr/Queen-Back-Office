package fr.insee.queen.application.utils.dummy;

import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@RequiredArgsConstructor
public class AuthenticationFakeHelper implements AuthenticationHelper {
    private final Authentication authenticationUser;

    @Override
    public String getUserToken() {
        if(authenticationUser instanceof JwtAuthenticationToken userOidc) {
            return userOidc.getToken().getTokenValue();
        }
        return null;
    }

    @Override
    public Authentication getAuthenticationPrincipal() {
        return authenticationUser;
    }
}
