package fr.insee.queen.api.controller;

import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.input.CampaignInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultDto;
import fr.insee.queen.api.exception.CampaignDeletionException;
import fr.insee.queen.api.service.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* CampaignController is the Controller used to manage campaigns
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
public class CampaignController {
	private final HabilitationService habilitationService;
	private final IntegrationService integrationService;
	@Value("${application.pilotage.integration-override}")
	private final String integrationOverride;
	/**
	* The campaign repository used to access 'campaign' table in DB 
	*/
	private final CampaignService campaignService;
	private final QuestionnaireModelService questionnaireService;
	private final PilotageApiService pilotageApiService;

	/**
	 * This method is used to get all campaigns
	 * @return List of all {@link CampaignSummaryDto}
	 */
	@Operation(summary = "Get list of all campaigns")
	@GetMapping(path = "/admin/campaigns")
	public List<CampaignSummaryDto> getListCampaign() {
		String userId = habilitationService.getUserId();
		log.info("Admin {} request all campaigns", userId);
		return campaignService.getAllCampaigns();
	}

	/**
	* This method return all user related campaigns
	* @return List of  {@link CampaignSummaryDto}
	*/

	@Operation(summary = "Get list of user related campaigns")
	@GetMapping(path = "/campaigns")
	public List<CampaignSummaryDto> getInterviewerCampaignList(HttpServletRequest request) {

		String userId = habilitationService.getUserId();
		log.info("User {} need his campaigns", userId);

		List<CampaignSummaryDto> campaigns;

		if (integrationOverride != null && integrationOverride.equals("true")) {
			campaigns = campaignService.getAllCampaigns();
			log.info("{} campaign(s) found for {}", campaigns.size(), userId);
			return campaigns;
		}

		campaigns = pilotageApiService.getInterviewerCampaigns(request);
		// add id
		campaigns.forEach(camp -> camp.questionnaireIds(questionnaireService.findAllQuestionnaireIdDtoByCampaignId(camp.id())));
		log.info("{} campaign(s) found for {}", campaigns.size(), userId);
		return campaigns;
	}
	
	/**
	* This method is used to post a new campaign
	* @param campaignInputDto the value to create
	*
	*/
	@Operation(summary = "Create a campaign")
	@PostMapping(path = "/campaigns")
	public void createCampaign(@RequestBody CampaignInputDto campaignInputDto) {
		String userId = habilitationService.getUserId();
		log.info("User {} requests campaign {} creation", userId, campaignInputDto.id());
		campaignService.createCampaign(campaignInputDto);
	}
	
	/**
	* This method is used to post a new campaign
	* 
	* @param file the integration zip file
	* @return {@link HttpStatus 400} if questionnaire is not found, else {@link HttpStatus 200}
	* 
	*/
	@Operation(summary = "Integrates the context of a campaign")
	@PostMapping(path = "/campaign/context")
	public IntegrationResultDto integrateContext(@RequestParam("file") MultipartFile file) {
		String userId = habilitationService.getUserId();
		log.info("User {} requests campaign creation via context ", userId);
		return integrationService.integrateContext(file);
	}

	/**
	 * This method is used to delete a campaign
	 *
	 * @param force force the full delettion of campaign
	 * @param request http servlet request object
	 * @param campaignId campaign id
	 */
	@Operation(summary = "Delete a campaign")
	@DeleteMapping(path = "/campaign/{id}")
	public void deleteCampaignById(@RequestParam("force") boolean force,
										 HttpServletRequest request,
										 @PathVariable(value = "id") String campaignId) {
		String userId = habilitationService.getUserId();
		log.info("Admin {} requests deletion of campaign {}", userId, campaignId);

		if(force ||
				(integrationOverride != null && integrationOverride.equals("true")) ||
				pilotageApiService.isClosed(campaignId,request)) {
			campaignService.delete(campaignId);
			log.info("Campaign with id {} deleted", campaignId);
			return;
		}
		throw new CampaignDeletionException(String.format("Unable to delete campaign %s, campaign isn't closed", campaignId));
	}
}
