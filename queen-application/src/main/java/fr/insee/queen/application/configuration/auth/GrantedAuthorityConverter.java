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
    public static final String REALM_ACCESS_ROLE = "roles";
    public static final String REALM_ACCESS = "realm_access";

    private final Map<String, List<SimpleGrantedAuthority>> roles;
    private final OidcProperties oidcProperties;

    public GrantedAuthorityConverter(OidcProperties oidcProperties, RoleProperties roleProperties) {
        this.roles = new HashMap<>();
        this.oidcProperties = oidcProperties;
        initRole(roleProperties.surveyUnit(), AuthorityRoleEnum.SURVEY_UNIT);
        initRole(roleProperties.interviewer(), AuthorityRoleEnum.INTERVIEWER);
        initRole(roleProperties.reviewer(), AuthorityRoleEnum.REVIEWER);
        initRole(roleProperties.reviewerAlternative(), AuthorityRoleEnum.REVIEWER_ALTERNATIVE);
        initRole(roleProperties.admin(), AuthorityRoleEnum.ADMIN);
        initRole(roleProperties.webclient(), AuthorityRoleEnum.WEBCLIENT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        List<String> userRoles = getUserRoles(jwt);

        return userRoles.stream()
                .filter(this.roles::containsKey)
                .map(this.roles::get)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void initRole(String configRole, AuthorityRoleEnum authorityRole)  {
        // config role is not set
        if(configRole == null || configRole.isBlank()) {
            return;
        }

        this.roles.compute(configRole, (key, grantedAuthorities) -> {
            if(grantedAuthorities == null) {
                grantedAuthorities = new ArrayList<>();
            }
            grantedAuthorities.add(new SimpleGrantedAuthority(authorityRole.securityRole()));
            return grantedAuthorities;
        });
    }

    @SuppressWarnings("unchecked")
    private List<String> getUserRoles(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();

        if(oidcProperties.roleClaim().isEmpty()) {
            Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
            return (List<String>) realmAccess.get(REALM_ACCESS_ROLE);
        }
        return (List<String>) claims.get(oidcProperties.roleClaim());
    }
}