package fr.insee.queen.application.configuration.auth;

import fr.insee.queen.application.configuration.properties.OidcProperties;
import fr.insee.queen.application.configuration.properties.RoleProperties;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    public static final String REALM_ACCESS = "realm_access";
    public static final String ROLES = "roles";

    private final OidcProperties oidcProperties;
    private final RoleProperties roleProperties;

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        List<String> roles = getRoles(jwt);

        return roles.stream()
                .map(role -> {
                    if(role == null || role.isEmpty()) {
                        return null;
                    }
                    if (role.equals(roleProperties.surveyUnit())) {
                        return new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.SURVEY_UNIT);
                    }
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

    @SuppressWarnings("unchecked")
    private List<String> getRoles(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();

        if(oidcProperties.roleClaim().isEmpty()) {
            Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
            return (List<String>) realmAccess.get(ROLES);
        }
        return (List<String>) claims.get(oidcProperties.roleClaim());
    }
}