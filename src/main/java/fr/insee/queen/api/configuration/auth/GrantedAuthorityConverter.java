package fr.insee.queen.api.configuration.auth;

import fr.insee.queen.api.configuration.properties.KeycloakProperties;
import fr.insee.queen.api.configuration.properties.RoleProperties;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String REALM_ACCESS_ROLE = "roles";
    private static final String REALM_ACCESS = "realm_access";
    private final KeycloakProperties keycloakProperties;
    private final RoleProperties roleProperties;

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        Map<String, Object> realmAccess = (Map<String, Object>) claims.get(REALM_ACCESS);
        List<String> roles = (List<String>) realmAccess.get(REALM_ACCESS_ROLE);

        if (keycloakProperties.additionalRealm() != null) {
            Map<String, Object> additionalRealmAccess = (Map<String, Object>) claims.get(keycloakProperties.additionalRealm());
            if (additionalRealmAccess != null && additionalRealmAccess.containsKey(REALM_ACCESS_ROLE)) {
                roles.addAll((List<String>) additionalRealmAccess.get(REALM_ACCESS_ROLE));
            }
        }

        return roles.stream()
                .map(role -> {
                    if (role.equals(roleProperties.reviewer())) {
                        return new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.REVIEWER);
                    }
                    if (role.equals(roleProperties.reviewerAlternative())) {
                        return new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.REVIEWER_ALTERNATIVE);
                    }
                    if (role.equals(roleProperties.interviewer())) {
                        return new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.INTERVIEWER);
                    }
                    if (role.equals(roleProperties.admin())) {
                        return new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.ADMIN);
                    }
                    if (role.equals(roleProperties.webclient())) {
                        return new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.WEBCLIENT);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}