package fr.insee.queen.api.configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import fr.insee.queen.api.constants.Constants;



@ConditionalOnExpression( "'${fr.insee.queen.application.mode}' == 'KeyCloak'")
@KeycloakConfiguration
public class KeycloakConfig extends KeycloakWebSecurityConfigurerAdapter {

	@Value("${fr.insee.queen.interviewer.role:#{null}}")
	private String role;
	
	/**
     * Specific configuration for keycloak(add filter, etc)
     * @param http
     * @throws Exception
     */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
			// disable csrf because of API mode
			.csrf().disable()
			.sessionManagement()
            // use previously declared bean
               .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
               .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // keycloak filters for securisation
               .and()
                   .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
                   .addFilterBefore(keycloakAuthenticationProcessingFilter(), X509AuthenticationFilter.class)
                   .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
               
            // delegate logout endpoint to spring security

               .and()
                   .logout()
                   .addLogoutHandler(keycloakLogoutHandler())
                   .logoutUrl("/logout").logoutSuccessHandler(
                   // logout handler for API
                   (HttpServletRequest request, HttpServletResponse response, Authentication authentication) ->
                           response.setStatus(HttpServletResponse.SC_OK)
        		   )
               .and()
                   	// manage routes securisation
                   	.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
                   	// configuration for Swagger
       				.antMatchers("/swagger-ui.html/**", "/v2/api-docs","/csrf", "/", "/webjars/**", "/swagger-resources/**").permitAll()
       				.antMatchers("/environnement", "/healthcheck").permitAll()
                   	// configuration for endpoints
       				.antMatchers(Constants.API_OPERATIONS).hasRole(role)
       				.antMatchers(Constants.API_OPERATIONS_REPORTING_UNITS).hasRole(role)
					.antMatchers(Constants.API_OPERATIONS_QUESTIONAIRE).hasRole(role)
					.antMatchers(Constants.API_OPERATIONS_REQUIRED_NOMENCLATURES).hasRole(role)
					.antMatchers(Constants.API_REPORTING_UNIT_DATA).hasRole(role)
					.antMatchers(Constants.API_REPORTING_UNIT_COMMENT).hasRole(role)
					.antMatchers(Constants.API_NOMENCLATURE).hasRole(role)
					.anyRequest().denyAll(); 
	}
	
	/**
	 * Registers the KeycloakAuthenticationProvider with the authentication
	 * manager.
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) {
		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
		auth.authenticationProvider(keycloakAuthenticationProvider);
	}
	
	/**
     * Required to handle spring boot configurations
     * @return
     */
    @Bean
    @ConditionalOnExpression( "'${fr.insee.queen.application.mode}' == 'KeyCloak'")
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
   }
    
    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @ConditionalOnExpression( "'${fr.insee.queen.application.mode}' == 'KeyCloak'")
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        // required for bearer-only applications.
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());

    }
}