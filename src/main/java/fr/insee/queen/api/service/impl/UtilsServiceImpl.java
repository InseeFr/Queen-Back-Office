package fr.insee.queen.api.service.impl;

import java.util.LinkedHashMap;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import fr.insee.queen.api.controller.QuestionnaireModelController;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import fr.insee.queen.api.configuration.ApplicationProperties.Mode;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import fr.insee.queen.api.service.UtilsService;
import liquibase.pro.packaged.T;

@Service
public class UtilsServiceImpl implements UtilsService{

	private static final Logger LOGGER = LoggerFactory.getLogger(UtilsServiceImpl.class);

	@Value("${fr.insee.queen.pilotage.service.url.scheme:#{null}}")
	private String pilotageScheme;
	
	@Value("${fr.insee.queen.pilotage.service.url.host:#{null}}")
	private String pilotageHost;
	
	@Value("${fr.insee.queen.pilotage.service.url.port:#{null}}")
	private String pilotagePort;
	
	@Value("${fr.insee.queen.pilotage.integration.override:#{null}}")
	private String integrationOverride;
	

	@Autowired
	Environment environment;
		
	@Autowired
	ApplicationProperties applicationProperties;
	
	@Autowired
	SurveyUnitRepository surveyUnitRepository;
	
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
		final String uriPilotageFilter = pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + Constants.API_PEARLJAM_SURVEYUNITS;
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
	public boolean checkHabilitation(HttpServletRequest request, String suId, String role){

		LOGGER.info("Starting check habilitation of user {} with role {} to access survey-unit {} ",request.getRemoteUser(),role,suId);

		if(integrationOverride != null && integrationOverride.equals("true")) {
			return true;
		}
		if(role.equals(Constants.INTERVIEWER))
			role = "";
		else if(role.equals(Constants.REVIEWER))
			role= Constants.REVIEWER;
		else 
			return false;
		
		String campaignId = "";
		Optional<SurveyUnit> su = surveyUnitRepository.findById(suId);
		if(su.isPresent()) {
			campaignId = su.get().getCampaign().getId();
		}
		
		String idep = getIdepFromToken(request);
		
		final String uriPilotageFilter = pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + Constants.API_HABILITATION + "?id=" + suId
				+ "&role=" + role + "&campaign=" + campaignId + "&idep=" + idep;
		String authTokenHeader = request.getHeader(Constants.AUTHORIZATION);
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(Constants.AUTHORIZATION, authTokenHeader);
		ResponseEntity<Object> resp = restTemplate.exchange(uriPilotageFilter, HttpMethod.GET, new HttpEntity<T>(headers), Object.class);
		return Boolean.TRUE.equals(((LinkedHashMap<String, Boolean>) resp.getBody()).get("habilitated"));
	}
	
	public String getIdepFromToken(HttpServletRequest request) {
		if (!applicationProperties.getMode().equals(Mode.noauth)) {
			return request.getRemoteUser();
		}
		return "";
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
