package fr.insee.queen.api.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

public interface UtilsService {
	
	/**
	 * This method retrieve the UserId passed in the HttpServletRequest
	 * @param HttpServletRequest
	 * @return String of UserId
	 */
	String getUserId(HttpServletRequest request);
	
	/**
	 * This method retrieve the data from the Pilotage API for the current user
	 * @param HttpServletRequest
	 * @return String of UserId
	 */
	ResponseEntity<Object> getSuFromPilotage(HttpServletRequest request);
	
	boolean checkHabilitation(HttpServletRequest request, String suId);

	
	boolean isDevProfile();
	
	public boolean isTestProfile();
}
