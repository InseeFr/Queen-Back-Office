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
	 * This method retrieve the data from the PearlJam API for the current user
	 * @param HttpServletRequest
	 * @return String of UserId
	 */
	ResponseEntity<Object> getSuFromPearlJam(HttpServletRequest request);

}
