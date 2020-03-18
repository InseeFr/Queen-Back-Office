package fr.insee.queen.queen.configuration;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	private Environment environment;

	protected boolean isDevelopment() {
		return ArrayUtils.contains(environment.getActiveProfiles(), "dev") || ArrayUtils.contains(environment.getActiveProfiles(), "test");
	}
	protected boolean isSercureCookie() {
		return true;
	}
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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
	    http
	    	.csrf()
	        .disable()
	        .headers()
	        .frameOptions()
	        .disable()
	    .and()
	    	.requestCache()
			.requestCache(new NullRequestCache());
        if (isDevelopment()) 
        {
	    	http.httpBasic().authenticationEntryPoint(unauthorizedEntryPoint());
	    }else {
	    	http.httpBasic().authenticationEntryPoint(unauthorizedEntryPoint());
	    }
        
        http.authorizeRequests()
        .antMatchers("/api/operation/**").hasRole("enqueteur")
        .anyRequest().denyAll();
    }
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		if (isDevelopment()) 
        {
			auth.inMemoryAuthentication()
			.withUser("admin").password("{noop}a").roles("enqueteur").and()
			.withUser("noWrite").password("{noop}a").roles();
        }
    }

	public AuthenticationEntryPoint unauthorizedEntryPoint() {
		return (request, response, authException) -> {
			response.addHeader("WWW-Authenticate", "BasicCustom realm=\"MicroService\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
		};
	}
}
