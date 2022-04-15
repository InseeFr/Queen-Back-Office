package fr.insee.queen.api.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import fr.insee.queen.api.dto.campaign.CampaignResponseDto;

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
	
	/**
	 * This method retrieve the campaigns from the Pilotage API for the current user
	 * @param HttpServletRequest
	 * @return List of {@link CampaignResponseDto}
	 */
	List<CampaignResponseDto> getInterviewerCampaigns(HttpServletRequest request);

	boolean checkHabilitation(HttpServletRequest request, String suId, String role);
	
	boolean isDevProfile();
	
	public boolean isTestProfile();
}
