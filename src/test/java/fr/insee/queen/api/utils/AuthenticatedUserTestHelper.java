package fr.insee.queen.api.utils;

import fr.insee.queen.api.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.api.constants.Constants;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthenticatedUserTestHelper {

    public Authentication getAuthenticatedUser() {
        return getAuthenticatedUser(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.REVIEWER);
    }

    public Authentication getAuthenticatedUser(AuthorityRoleEnum... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(AuthorityRoleEnum role : roles) {
            authorities.add(new SimpleGrantedAuthority(Constants.ROLE_PREFIX + role.name()));
        }

        Map<String, Object> headers = Map.of("typ", "JWT");
        Map<String, Object> claims = Map.of("preferred_username", "dupont-identifier", "name", "Jean Dupont");

        Jwt jwt = new Jwt("token-value", Instant.MIN, Instant.MAX, headers, claims);
        return new JwtAuthenticationToken(jwt, authorities, "Jean Dupont");
    }

    public Authentication getNotAuthenticatedUser() {
        Map<String, String> principal = new HashMap<>();
        Authentication auth = new AnonymousAuthenticationToken("id", principal, List.of(new SimpleGrantedAuthority(Constants.ROLE_PREFIX + "ANONYMOUS")));
        auth.setAuthenticated(false);
        return auth;
    }
}
