package fr.insee.queen.api.configuration.auth;

import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.constants.Constants;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

/**
 * Handle http requests authorization when app use keycloak/basic authentication
 */
@Component
@AllArgsConstructor
public class ApiRequestsAuthorizer {
    private final ApplicationProperties applicationProperties;

    /**
     *
     * @param configurer configuration object used to configure the http requests authorization
     * @return the configuration object with http requests authorization configured
     */
    public AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry handleApiRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) {
        return configurer
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .requestMatchers(applicationProperties.publicUrls()).permitAll()
                // healtcheck
                .requestMatchers(HttpMethod.GET, Constants.API_HEALTH_CHECK).permitAll()
                // actuator (actuator metrics are disabled by default)
                .requestMatchers(HttpMethod.GET, Constants.API_ACTUATOR).permitAll()
                .anyRequest().authenticated();
    }
}
