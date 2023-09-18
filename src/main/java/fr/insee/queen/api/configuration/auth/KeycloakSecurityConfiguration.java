package fr.insee.queen.api.configuration.auth;

import fr.insee.queen.api.configuration.properties.KeycloakProperties;
import fr.insee.queen.api.constants.Constants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spring security configuration when using KEYCLOAK auth
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "application.auth", havingValue = "KEYCLOAK")
public class KeycloakSecurityConfiguration {
    private final KeycloakProperties keycloakProperties;

    private static final String REALM_ACCESS_ROLE = "roles";

    private static final String REALM_ACCESS = "realm_access";

    public KeycloakSecurityConfiguration(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    /**
     * Configure spring security filter chain to handle keycloak authentication
     * @param http Http Security Object
     * @param apiRequestsAuthorizer HTTP requests authorizer
     * @return the spring security filter
     * @throws Exception exception
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, ApiRequestsAuthorizer apiRequestsAuthorizer) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(apiRequestsAuthorizer::handleApiRequests)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setPrincipalClaimName("name");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }

    Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        return new Converter<Jwt, Collection<GrantedAuthority>>() {
            @SuppressWarnings("unchecked")
            @Override
            public Collection<GrantedAuthority> convert(Jwt jwt) {
                Map<String, Object> claims = jwt.getClaims();
                Map<String, Object> realmAccess = (Map<String, Object>) claims.get(REALM_ACCESS);
                List<String> roles = (List<String>) realmAccess.get(REALM_ACCESS_ROLE);

                if(keycloakProperties.additionalRealm() != null) {
                    Map<String, Object> additionalRealmAccess = (Map<String, Object>) claims.get(keycloakProperties.additionalRealm());
                    if(additionalRealmAccess != null && additionalRealmAccess.containsKey(REALM_ACCESS_ROLE)) {
                        roles.addAll((List<String>) additionalRealmAccess.get(REALM_ACCESS_ROLE));
                    }
                }

                return roles.stream()
                        .map(r -> new SimpleGrantedAuthority(Constants.ROLE_PREFIX + r)).collect(Collectors
                        .toCollection(ArrayList::new));
            }
        };
    }
}

