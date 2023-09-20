package fr.insee.queen.api.controller;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.controller.utils.AuthenticationHelper;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.input.CampaignInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultDto;
import fr.insee.queen.api.exception.CampaignDeletionException;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.IntegrationService;
import fr.insee.queen.api.service.PilotageApiService;
import fr.insee.queen.api.service.QuestionnaireModelService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
	private final AuthenticationHelper authHelper;
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
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	public List<CampaignSummaryDto> getListCampaign(Authentication auth) {
		String userId = authHelper.getUserId(auth);
		log.info("Admin {} request all campaigns", userId);
		return campaignService.getAllCampaigns();
	}

	/**
	* This method return all user related campaigns
	* @return List of  {@link CampaignSummaryDto}
	*/

	@Operation(summary = "Get list of user related campaigns")
	@GetMapping(path = "/campaigns")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public List<CampaignSummaryDto> getInterviewerCampaignList(Authentication auth) {

		String userId = authHelper.getUserId(auth);
		log.info("User {} need his campaigns", userId);

		List<CampaignSummaryDto> campaigns;

		if (integrationOverride != null && integrationOverride.equals("true")) {
			campaigns = campaignService.getAllCampaigns();
			log.info("{} campaign(s) found for {}", campaigns.size(), userId);
			return campaigns;
		}

		String authToken = authHelper.getAuthToken(auth);
		campaigns = pilotageApiService.getInterviewerCampaigns(authToken);
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
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	public HttpStatus createCampaign(@Valid @RequestBody CampaignInputDto campaignInputDto,
							   Authentication auth) {
		String userId = authHelper.getUserId(auth);
		log.info("User {} requests campaign {} creation", userId, campaignInputDto.id());
		campaignService.createCampaign(campaignInputDto);
		return HttpStatus.CREATED;
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
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	public IntegrationResultDto integrateContext(@NotNull @RequestParam("file") MultipartFile file,
												 Authentication auth) {
		String userId = authHelper.getUserId(auth);
		log.info("User {} requests campaign creation via context ", userId);
		return integrationService.integrateContext(file);
	}

	/**
	 * This method is used to delete a campaign
	 *
	 * @param force force the full delettion of campaign
	 * @param campaignId campaign id
	 */
	@Operation(summary = "Delete a campaign")
	@DeleteMapping(path = "/campaign/{id}")
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	public HttpStatus deleteCampaignById(@RequestParam("force") boolean force,
								   @NotBlank @PathVariable(value = "id") String campaignId,
								   Authentication auth) {
		String userId = auth.getName();
		log.info("Admin {} requests deletion of campaign {}", userId, campaignId);

		String authToken = authHelper.getAuthToken(auth);
		if(force ||
				(integrationOverride != null && integrationOverride.equals("true")) ||
				pilotageApiService.isClosed(campaignId, authToken)) {
			campaignService.delete(campaignId);
			log.info("Campaign with id {} deleted", campaignId);
			return HttpStatus.NO_CONTENT;
		}
		throw new CampaignDeletionException(String.format("Unable to delete campaign %s, campaign isn't closed", campaignId));
	}
}
