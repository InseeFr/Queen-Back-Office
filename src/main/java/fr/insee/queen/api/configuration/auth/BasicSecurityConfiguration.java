package fr.insee.queen.api.configuration.auth;

import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.configuration.properties.BasicAuthProperties;
import fr.insee.queen.api.constants.Constants;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

/**
 * Spring security configuration when using BASIC auth
 */
@ConditionalOnProperty(name = "application.auth", havingValue = "BASIC")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class BasicSecurityConfiguration {
    private final PublicSecurityFilterChain publicSecurityFilterChainConfiguration;
    /**
     * Configure spring security filter chain to handle basic authentication
     * @param http Http Security Object
     * @return the spring security filter
     * @throws Exception exception if problem during configuring urls
     */
    @Bean
    @Order(2)
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(customizer -> customizer.authenticationEntryPoint(unauthorizedEntryPoint()))
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
                    .requestMatchers(HttpMethod.OPTIONS).permitAll()
                    .requestMatchers(HttpMethod.GET, Constants.API_HEALTH_CHECK).permitAll()
                    // actuator (actuator metrics are disabled by default)
                    .requestMatchers(HttpMethod.GET, Constants.API_ACTUATOR).permitAll()
                    .anyRequest()
                        .authenticated());
        return http.build();
    }

    @Bean
    @Order(1)
    SecurityFilterChain filterPublicUrlsChain(HttpSecurity http, ApplicationProperties applicationProperties) throws Exception {
        return publicSecurityFilterChainConfiguration.buildSecurityPublicFilterChain(http, applicationProperties.publicUrls());
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

