package fr.insee.queen.api.configuration.auth;

import fr.insee.queen.api.configuration.properties.KeycloakProperties;
import fr.insee.queen.api.configuration.properties.RoleProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

/**
 * Spring security configuration when using KEYCLOAK auth
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "application.auth", havingValue = "KEYCLOAK")
public class KeycloakSecurityConfiguration {

    /**
     * Configure spring security filter chain to handle keycloak authentication
     * @param http Http Security Object
     * @param apiRequestsAuthorizer HTTP requests authorizer
     * @return the spring security filter
     * @throws Exception exception
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, ApiRequestsAuthorizer apiRequestsAuthorizer,
                                    KeycloakProperties keycloakProperties, RoleProperties roleProperties) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(apiRequestsAuthorizer::handleApiRequests)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(keycloakProperties, roleProperties))));
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(KeycloakProperties keycloakProperties, RoleProperties roleProperties) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setPrincipalClaimName("name");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter(keycloakProperties, roleProperties));
        return jwtAuthenticationConverter;
    }

    Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter(KeycloakProperties keycloakProperties, RoleProperties roleProperties) {
        return new GrantedAuthorityConverter(keycloakProperties, roleProperties);
    }
}

