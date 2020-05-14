package fr.insee.queen.api.configuration;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.savedrequest.NullRequestCache;

import fr.insee.queen.api.configuration.ApplicationProperties.Mode;


/**
 * SecurityConfiguration is the class using to configure security.<br>
 * 3 ways to authenticated : <br>
 * 0 - without authentication,<br>
 * 1 - basic authentication <br>
 * 2 - and keycloak authentication <br>
 * 
 * @author Claudel Benjamin
 * 
 */
@Configuration
@EnableWebSecurity
@ConditionalOnExpression( "'${fr.insee.queen.application.mode}' == 'Basic' or '${fr.insee.queen.application.mode}' == 'NoAuth'" )
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	/**
	 * The environment define in Spring application Generate with the application
	 * property environment
	 */
	@Autowired
	private Environment environment;

	@Autowired
	private ApplicationProperties applicationProperties;

	/**
	 * This method check if environment is development or test
	 * 
	 * @return true if environment matchs
	 */
	protected boolean isDevelopment() {
		return ArrayUtils.contains(environment.getActiveProfiles(), "dev")
				|| ArrayUtils.contains(environment.getActiveProfiles(), "test");
	}

	/**
	 * This method configure the WEB security access
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		System.setProperty("keycloak.enabled", applicationProperties.getMode() != Mode.Keycloak?"false":"true");
		web.ignoring()
			.antMatchers(HttpMethod.OPTIONS, "/**")
			.antMatchers("/i18n/**")
			.antMatchers("/content/**")
			.antMatchers("/test/**")
			.antMatchers("/h2-console/**")
			.antMatchers("/v2/**")
			.antMatchers("/swagger-resources/**")
			.antMatchers("/webjars/**")
			.antMatchers("/actuator/**")
			.antMatchers("/login.html")
			.antMatchers("/login")
			.antMatchers("/swagger-ui.html");
	}

	/**
	 * This method configure the HTTP security access
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().headers().frameOptions().disable().and().requestCache()
				.requestCache(new NullRequestCache());
		switch (this.applicationProperties.getMode()) {
			case Basic:
				http.httpBasic().authenticationEntryPoint(unauthorizedEntryPoint());
				http.authorizeRequests().antMatchers("/operations").hasRole("investigator")
					.antMatchers("/operation/{idOperation}/reporting-units").hasRole("investigator")
					.antMatchers("/operation/{idOperation}/questionnaire").hasRole("investigator")
					.antMatchers("/operation/{id}/required-nomenclatures").hasRole("investigator")
					.antMatchers("/reporting-unit/{id}/data").hasRole("investigator")
					.antMatchers("/reporting-unit/{id}/comment").hasRole("investigator")
					.antMatchers("/nomenclature/{id}").hasRole("investigator")
					.anyRequest().denyAll(); 
				break;
			default:
				http.httpBasic().disable();
				http.authorizeRequests()
				.antMatchers("/operations",
						"/operation/{idOperation}/reporting-units",
						"/operation/{idOperation}/questionnaire",
						"/operation/{id}/required-nomenclatures",
						"/reporting-unit/{id}/data",
						"/reporting-unit/{id}/comment",
						"/nomenclature/{id}").permitAll();
				break;
		}
	}

	/**
	 * This method configure the authentication manager for DEV and TEST accesses
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		if (isDevelopment()) {
			switch (this.applicationProperties.getMode()) {
			case Basic:
				auth.inMemoryAuthentication().withUser("admin").password("{noop}a").roles("investigator").and()
				.withUser("noWrite").password("{noop}a").roles();
				break;
			case NoAuth:
				break;
			default:
				break;
			}
		}
	}
	
	@Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		 return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

    @Bean
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
    	return new HttpSessionManager();
    }
	/**
	 * This method configure the unauthorized accesses
	 */
	public AuthenticationEntryPoint unauthorizedEntryPoint() {
		return (request, response, authException) -> {
			response.addHeader("WWW-Authenticate", "BasicCustom realm=\"MicroService\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
		};
	}

}
