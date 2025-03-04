package fr.insee.queen.application.utils.dummy;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationFakeHelper implements AuthenticationHelper {
    @Setter
    private Authentication authenticationUser;

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

    @Override
    public boolean hasRole(AuthorityRoleEnum... roles) {
        return getAuthenticationPrincipal().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(AuthorityRoleEnum::fromAuthority)
                .anyMatch(role -> Arrays.asList(roles).contains(role));
    }
}
