package fr.insee.queen.application.configuration.auth;

import fr.insee.queen.application.configuration.properties.ApplicationProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "false")
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class NoAuthSecurityConfiguration {
    private final PublicSecurityFilterChain publicSecurityFilterChainConfiguration;

    /**
     * Configure spring security filter chain when no authentication
     *
     * @param http Http Security Object
     * @return the spring security filter
     * @throws Exception exception
     */
    @Bean
    @Order(2)
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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
                .anonymous(anonymousConfig -> anonymousConfig
                        .authorities("ROLE_ADMIN"))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(1)
    protected SecurityFilterChain filterPublicUrlsChain(HttpSecurity http, ApplicationProperties applicationProperties) throws Exception {
        return publicSecurityFilterChainConfiguration.buildSecurityPublicFilterChain(http, applicationProperties.publicUrls());
    }

}