package fr.insee.queen.application.web.authentication;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationUserHelper implements AuthenticationHelper {
    @Override
    public String getUserToken() {
        if(getAuthenticationPrincipal() instanceof JwtAuthenticationToken auth) {
            return auth.getToken().getTokenValue();
        }
        throw new AuthenticationTokenException("Cannot retrieve token for the user. Ensure you have not disabled oidc with pilotage api enabled");
    }

    @Override
    public Authentication getAuthenticationPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean hasRole(AuthorityRoleEnum... roles) {
        return getAuthenticationPrincipal()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(AuthorityRoleEnum::fromAuthority)
                .anyMatch(role -> Arrays.asList(roles).contains(role));
    }
}

