package fr.insee.queen.api.utils;

import fr.insee.queen.api.constants.Constants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class AuthenticatedUserTestHelper {

    public JwtAuthenticationToken getAuthenticatedUser() {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(Constants.INTERVIEWER), new SimpleGrantedAuthority(Constants.REVIEWER));

        Map<String, Object> headers = Map.of("typ", "JWT");
        Map<String, Object> claims = Map.of("preferred_username", "dupont-identifier", "name", "Jean Dupont");

        Jwt jwt = new Jwt("token-value", Instant.MIN, Instant.MAX, headers, claims);
        return new JwtAuthenticationToken(jwt, authorities, "Jean Dupont");
    }
}
