package fr.insee.queen.api.configuration.auth;

import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.configuration.properties.AuthEnumProperties;
import fr.insee.queen.api.configuration.properties.KeycloakProperties;
import fr.insee.queen.api.configuration.properties.RoleProperties;
import fr.insee.queen.api.constants.Constants;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.util.Collection;

/**
 * Spring security configuration when using KEYCLOAK auth
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "application.auth", havingValue = "KEYCLOAK")
@AllArgsConstructor
public class KeycloakSecurityConfiguration {
    private final PublicSecurityFilterChain publicSecurityFilterChainConfiguration;

    /**
     * Configure spring security filter chain to handle keycloak authentication
     * @param http Http Security Object
     * @return the spring security filter
     * @throws Exception exception
     */
    @Bean
    @Order(2)
    SecurityFilterChain filterChain(HttpSecurity http,
                                    KeycloakProperties keycloakProperties, RoleProperties roleProperties) throws Exception {
        return http
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(headers -> headers
                        .xssProtection(xssConfig -> xssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                        .contentSecurityPolicy(cspConfig -> cspConfig
                                .policyDirectives("default-src 'none'")
                        )
                        .referrerPolicy(referrerPolicy ->
                            referrerPolicy
                                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
                ))
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, Constants.API_HEALTH_CHECK).permitAll()
                        // actuator (actuator metrics are disabled by default)
                        .requestMatchers(HttpMethod.GET, Constants.API_ACTUATOR).permitAll()
                        .anyRequest()
                            .authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(keycloakProperties, roleProperties))))
                .build();
    }

    @Bean
    @Order(1)
    SecurityFilterChain filterPublicUrlsChain(HttpSecurity http, ApplicationProperties applicationProperties,
                                    KeycloakProperties keycloakProperties) throws Exception {
        String authorizedConnectionHost = applicationProperties.auth().equals(AuthEnumProperties.KEYCLOAK) ?
                " " + keycloakProperties.authServerHost() : "";
        return publicSecurityFilterChainConfiguration.buildSecurityPublicFilterChain(http, applicationProperties.publicUrls(), authorizedConnectionHost);
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

