package fr.insee.queen.api.configuration;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import fr.insee.queen.api.configuration.ApplicationProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.NullRequestCache;

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
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
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
		web.ignoring()
			.antMatchers(HttpMethod.OPTIONS, "/**")
			.antMatchers("/i18n/**")
			.antMatchers("/content/**")
			.antMatchers("/test/**")
			.antMatchers("/h2-console/**")
			.antMatchers("/v2/**")
			.antMatchers("/swagger-resources/**")
			.antMatchers("/webjars/**")
			.antMatchers("/swagger-ui.html")
			.antMatchers("/actuator/**")
			.antMatchers("/login.html")
			.antMatchers("/login")
			.antMatchers("/");
	}

	/**
	 * This method configure the HTTP security access
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().headers().frameOptions().disable().and().requestCache()
				.requestCache(new NullRequestCache());
		if (isDevelopment()) {
			switch (this.applicationProperties.getMode()) {
			case Basic:
				System.out.println(ApplicationProperties.Mode.Basic);
				http.httpBasic().authenticationEntryPoint(unauthorizedEntryPoint());
				http.authorizeRequests().antMatchers("/operations").hasRole("enqueteur")
					.antMatchers("/operation/{idOperation}/reporting-units").hasRole("enqueteur")
					.antMatchers("/operation/{idOperation}/questionnaire").hasRole("enqueteur")
					.antMatchers("/operation/{id}/required-nomenclatures").hasRole("enqueteur")
					.antMatchers("/reporting-unit/{id}/data").hasRole("enqueteur")
					.antMatchers("/reporting-unit/{id}/comment").hasRole("enqueteur")
					.antMatchers("/nomenclature/{id}").hasRole("enqueteur")
					.anyRequest().denyAll(); 
				break;
			case Keycloak:
				System.out.println(ApplicationProperties.Mode.Keycloak);
				break;
			default:
				System.out.println(ApplicationProperties.Mode.NoAuth);
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
			
		} else {
			switch (this.applicationProperties.getMode()) {
			case Basic:
				http.httpBasic().authenticationEntryPoint(unauthorizedEntryPoint());
				break;
			case Keycloak:
				System.out.println(ApplicationProperties.Mode.Keycloak);
				break;
			default:
				System.out.println(ApplicationProperties.Mode.NoAuth);
				break;
			}
		}

	}

	/**
	 * This method configure the authentication manager for DEV and TEST accesses
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		if (isDevelopment()) {
			auth.inMemoryAuthentication().withUser("admin").password("{noop}a").roles("enqueteur").and()
					.withUser("noWrite").password("{noop}a").roles();
		}else {
			
		}
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
