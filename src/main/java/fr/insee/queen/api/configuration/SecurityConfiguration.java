package fr.insee.queen.api.configuration;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.savedrequest.NullRequestCache;

import fr.insee.queen.api.configuration.ApplicationProperties.Mode;
import fr.insee.queen.api.constants.Constants;

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
@ConditionalOnExpression("'${fr.insee.queen.application.mode}'=='basic' or '${fr.insee.queen.application.mode}'=='noauth'")
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

	@Value("${fr.insee.queen.interviewer.role:#{null}}")
	private String role;

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
	 * This method configure the HTTP security access
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		System.setProperty("keycloak.enabled", applicationProperties.getMode() != Mode.keycloak ? "false" : "true");
		http.csrf().disable().headers().frameOptions().disable().and().requestCache()
				.requestCache(new NullRequestCache());
		if (this.applicationProperties.getMode() == Mode.basic) {
			http.httpBasic().authenticationEntryPoint(unauthorizedEntryPoint());
			http.authorizeRequests()
					// manage routes securisation
					.antMatchers(HttpMethod.OPTIONS).permitAll()
					// configuration for Swagger
					.antMatchers("/swagger-ui.html/**", "/v2/api-docs", "/csrf", "/", "/webjars/**", "/swagger-resources/**")
					.permitAll().antMatchers("/environnement", "/healthcheck").permitAll()
					.antMatchers(Constants.API_CAMPAIGNS).hasRole(role)
					.antMatchers(Constants.API_CAMPAIGN_CONTEXT).hasRole(role)
					.antMatchers(Constants.API_CAMPAIGN_ID_SURVEY_UNITS).hasRole(role)
					.antMatchers(Constants.API_CAMPAIGN_ID_SURVEY_UNIT).hasRole(role)
					.antMatchers(Constants.API_CAMPAIGN_ID_METADATA).hasRole(role)
					.antMatchers(Constants.API_CAMPAIGN_ID_QUESTIONAIRES).hasRole(role)
					.antMatchers(Constants.API_CAMPAIGN_ID_QUESTIONAIREID).hasRole(role)
					.antMatchers(Constants.API_CAMPAIGN_ID_REQUIREDNOMENCLATURES).hasRole(role)
					.antMatchers(Constants.API_SURVEYUNIT_ID).hasRole(role)
					.antMatchers(Constants.API_SURVEYUNIT_ID_DATA).hasRole(role)
					.antMatchers(Constants.API_SURVEYUNIT_ID_COMMENT).hasRole(role)
					.antMatchers(Constants.API_SURVEYUNIT_ID_STATEDATA).hasRole(role)
					.antMatchers(Constants.API_SURVEYUNIT_ID_DEPOSITPROOF).hasRole(role)
					.antMatchers(Constants.API_SURVEYUNIT_ID_PERSONALIZATION).hasRole(role)
					.antMatchers(Constants.API_NOMENCLATURE).hasRole(role)
					.antMatchers(Constants.API_NOMENCLATURE_ID).hasRole(role)
					.antMatchers(Constants.API_QUESTIONNAIRE_ID).hasRole(role)
					.antMatchers(Constants.API_QUESTIONNAIRE_ID_METADATA).hasRole(role)
					.antMatchers(Constants.API_QUESTIONNAIRE_ID_REQUIREDNOMENCLATURE).hasRole(role)
					.antMatchers(Constants.API_QUESTIONNAIREMODELS).hasRole(role)
					.antMatchers(Constants.API_PARADATAEVENT).hasRole(role)
					.antMatchers(Constants.API_CREATE_DATASET).hasRole(role)
					.anyRequest().denyAll();
			
		} else {
			http.httpBasic().disable();
			http.authorizeRequests()
          .antMatchers(Constants.API_CAMPAIGNS,
        		  Constants.API_CAMPAIGN_CONTEXT,
        		  Constants.API_CAMPAIGN_ID_SURVEY_UNITS,
        		  Constants.API_CAMPAIGN_ID_SURVEY_UNIT,
        		  Constants.API_CAMPAIGN_ID_METADATA,
        		  Constants.API_CAMPAIGN_ID_QUESTIONAIRES,
        		  Constants.API_CAMPAIGN_ID_QUESTIONAIREID,
        		  Constants.API_CAMPAIGN_ID_REQUIREDNOMENCLATURES,
        		  Constants.API_SURVEYUNIT_ID,
        		  Constants.API_SURVEYUNIT_ID_DATA,
        		  Constants.API_SURVEYUNIT_ID_COMMENT,
        		  Constants.API_SURVEYUNIT_ID_STATEDATA,
        		  Constants.API_SURVEYUNIT_ID_DEPOSITPROOF,
        		  Constants.API_SURVEYUNIT_ID_PERSONALIZATION,
        		  Constants.API_NOMENCLATURE,
        		  Constants.API_NOMENCLATURE_ID,
        		  Constants.API_QUESTIONNAIRE_ID,
        		  Constants.API_QUESTIONNAIRE_ID_METADATA,
        		  Constants.API_QUESTIONNAIRE_ID_REQUIREDNOMENCLATURE,
        		  Constants.API_QUESTIONNAIREMODELS,
        		  Constants.API_PARADATAEVENT,
        		  Constants.API_CREATE_DATASET)
					.permitAll();
		}
	}

	/**
	 * This method configure the authentication manager for DEV and TEST accesses
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		if (isDevelopment()) {
			switch (this.applicationProperties.getMode()) {
			case basic:
				auth.inMemoryAuthentication().withUser("INTW1").password("{noop}a").roles(role).and()
						.withUser("noWrite").password("{noop}a").roles();
				break;
			case noauth:
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
