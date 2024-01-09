package fr.insee.queen.application.configuration.auth;

import fr.insee.queen.application.configuration.properties.OidcProperties;
import fr.insee.queen.application.configuration.properties.RoleProperties;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final OidcProperties oidcProperties;
    private final RoleProperties roleProperties;

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        List<String> roles = (List<String>) claims.get(oidcProperties.roleClaim());

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