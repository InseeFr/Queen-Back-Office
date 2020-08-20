package fr.insee.queen.api.service;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fr.insee.queen.api.configuration.ApplicationProperties;
import fr.insee.queen.api.constants.Constants;
import liquibase.pro.packaged.T;

@Service
public class UtilsServiceImpl implements UtilsService{
	
	@Value("${fr.insee.queen.pearljam.url.scheme:#{null}}")
	private String pearlJamScheme;
	
	@Value("${fr.insee.queen.pearljam.url.host:#{null}}")
	private String pearlJamHost;
	
	@Value("${fr.insee.queen.pearljam.url.port:#{null}}")
	private String pearlJamPort;
		
	@Autowired
	ApplicationProperties applicationProperties;
	
	/**
	 * This method retrieve retrieve the UserId passed in the HttpServletRequest. 
	 * Three possible cases which depends of the authentication chosen.
	 * @param HttpServletRequest
	 * @return String of UserId
	 */
	public String getUserId(HttpServletRequest request) {
		String userId = null;
		switch (applicationProperties.getMode()) {
		case Basic:
			Object basic = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (basic instanceof UserDetails) {
				userId = ((UserDetails)basic).getUsername();
			} else {
				userId = basic.toString();
			}
			break;
		case Keycloak:
			KeycloakAuthenticationToken keycloak = (KeycloakAuthenticationToken) request.getUserPrincipal();
			userId = keycloak.getPrincipal().toString();
			break;
		default:
			userId = "GUEST";
			break;
		}
		return userId;
	}
	
	/**
	 * This method retrieve the data from the PearlJam API for the current user
	 * @param <T>
	 * @param HttpServletRequest
	 * @return String of UserId
	 */
	public ResponseEntity<Object> getSuFromPearlJam(HttpServletRequest request){
		final String uriPearlJamFilter = pearlJamScheme + "://" + pearlJamHost + ":" + pearlJamPort + Constants.API_PEARLJAM_SURVEY_UNITS;
		String authTokenHeader = request.getHeader("Authorization");
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", authTokenHeader);
		return restTemplate.exchange(uriPearlJamFilter, HttpMethod.GET, new HttpEntity<T>(headers), Object.class);
	}
}
