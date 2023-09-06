package fr.insee.queen.api.configuration.auth;

import fr.insee.queen.api.configuration.properties.BasicAuthProperties;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring security configuration when using BASIC auth
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "application.auth", havingValue = "BASIC")
public class BasicSecurityConfiguration {

    /**
     * Configure spring security filter chain to handle basic authentication
     * @param http Http Security Object
     * @param apiRequestsAuthorizer HTTP requests authorizer
     * @return the spring security filter
     * @throws Exception exception if problem during configuring urls
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, ApiRequestsAuthorizer apiRequestsAuthorizer) throws Exception {
        http.httpBasic(customizer -> customizer.authenticationEntryPoint(unauthorizedEntryPoint()));
        http.authorizeHttpRequests(apiRequestsAuthorizer::handleApiRequests);
        return http.build();
    }

    /**
     *
     * @param basicAuthProperties properties for basic authentication / user management
     * @return the service handling users
     */
    @Bean
    public UserDetailsService userDetailsService(BasicAuthProperties basicAuthProperties) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        basicAuthProperties.users().forEach(user ->
            manager.createUser(User.withUsername(user.username())
                    .password(String.format("{noop}%s", user.password()))
                    .roles(user.roles())
                    .build())
        );
        return manager;
    }

    /**
     * This method configure the unauthorized accesses
     * @return the unauthorized entry point
     */
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            response.addHeader("WWW-Authenticate", "BasicCustom realm=\"MicroService\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        };
    }

}

