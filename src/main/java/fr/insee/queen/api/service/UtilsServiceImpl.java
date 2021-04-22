package fr.insee.queen.api.service;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
	
	@Value("${fr.insee.queen.pilotage.service.url.scheme:#{null}}")
	private String pilotageScheme;
	
	@Value("${fr.insee.queen.pilotage.service.url.host:#{null}}")
	private String pilotageHost;
	
	@Value("${fr.insee.queen.pilotage.service.url.port:#{null}}")
	private String pilotagePort;
	
	@Autowired
	Environment environment;
		
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
		case basic:
			Object basic = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (basic instanceof UserDetails) {
				userId = ((UserDetails)basic).getUsername();
			} else {
				userId = basic.toString();
			}
			break;
		case keycloak:
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
	 * This method retrieve the data from the Pilotage API for the current user
	 * @param <T>
	 * @param HttpServletRequest
	 * @return String of UserId
	 */
	public ResponseEntity<Object> getSuFromPilotage(HttpServletRequest request){
		final String uriPilotageFilter = pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + Constants.API_PEARLJAM_SURVEY_UNITS;
		String authTokenHeader = request.getHeader(Constants.AUTHORIZATION);
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(Constants.AUTHORIZATION, authTokenHeader);
		return restTemplate.exchange(uriPilotageFilter, HttpMethod.GET, new HttpEntity<T>(headers), Object.class);
	}
	
	/**
	 * This method checks if the user has access to the suId with id "SUid"
	 * @param <T>
	 * @param HttpServletRequest
	 * @param String
	 * @return Boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean checkHabilitation(HttpServletRequest request, String suId){
		final String uriPilotageFilter = pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + Constants.API_HABILITATION + "?id=" + suId;
		String authTokenHeader = request.getHeader(Constants.AUTHORIZATION);
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(Constants.AUTHORIZATION, authTokenHeader);
		try {
			ResponseEntity<Object> resp = restTemplate.exchange(uriPilotageFilter, HttpMethod.GET, new HttpEntity<T>(headers), Object.class);
			return Boolean.TRUE.equals(((LinkedHashMap<String, Boolean>) resp.getBody()).get("habilitated"));
		}
		catch(Exception e) {
			return false;
		}
		
	}

	@Override
	public boolean isDevProfile() {
		for (final String profileName : environment.getActiveProfiles()) {
	        if("dev".equals(profileName)) return true;
	    }   
	    return false;
	}
	
	@Override
	public boolean isTestProfile() {
		for (final String profileName : environment.getActiveProfiles()) {
	        if("test".equals(profileName)) return true;
	    }   
	    return false;
	}
}
