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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Set<String> targetAuthorities = Arrays.stream(roles)
                .flatMap(role -> Stream.of(role.name(), role.securityRole()))
                .collect(Collectors.toSet());
        return getAuthenticationPrincipal().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(targetAuthorities::contains);
    }
}
