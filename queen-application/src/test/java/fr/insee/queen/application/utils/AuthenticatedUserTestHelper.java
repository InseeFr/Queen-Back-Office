package fr.insee.queen.application.utils;

import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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

    public JwtAuthenticationToken getAdminUser() {
        return getAuthenticatedUser(
                AuthorityRoleEnum.ADMIN,
                AuthorityRoleEnum.WEBCLIENT);
    }

    public JwtAuthenticationToken getNonAdminUser() {
        return getAuthenticatedUser(
                AuthorityRoleEnum.REVIEWER,
                AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
                AuthorityRoleEnum.INTERVIEWER,
                AuthorityRoleEnum.SURVEY_UNIT);
    }

    public JwtAuthenticationToken getManagerUser() {
        return getAuthenticatedUser(
                AuthorityRoleEnum.REVIEWER,
                AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
                AuthorityRoleEnum.INTERVIEWER);
    }

    public JwtAuthenticationToken getNonInterviewerUser() {
        return getAuthenticatedUser(
                AuthorityRoleEnum.REVIEWER,
                AuthorityRoleEnum.REVIEWER_ALTERNATIVE,
                AuthorityRoleEnum.SURVEY_UNIT);
    }

    public JwtAuthenticationToken getInterrogationUser() {
        return getAuthenticatedUser(AuthorityRoleEnum.SURVEY_UNIT);
    }

    public JwtAuthenticationToken getAuthenticatedUser(AuthorityRoleEnum... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (AuthorityRoleEnum role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.securityRole()));
        }

        Map<String, Object> headers = Map.of("typ", "JWT");
        Map<String, Object> claims = Map.of("preferred_username", "dupont-identifier", "name", "Jean Dupont");

        Jwt jwt = new Jwt("token-value", Instant.MIN, Instant.MAX, headers, claims);
        return new JwtAuthenticationToken(jwt, authorities, "dupont-identifier");
    }

    public AnonymousAuthenticationToken getNotAuthenticatedUser() {
        Map<String, String> principal = new HashMap<>();
        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken("id", principal, List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        auth.setAuthenticated(false);
        return auth;
    }
}
