package fr.insee.queen.api.controller.campaign;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.controller.utils.AuthenticationHelper;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.input.CampaignInputDto;
import fr.insee.queen.api.service.pilotage.PilotageService;
import fr.insee.queen.api.service.campaign.CampaignService;
import fr.insee.queen.api.service.exception.CampaignDeletionException;
import fr.insee.queen.api.service.questionnaire.QuestionnaireModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* CampaignController is the Controller used to manage campaigns
* 
* @author Claudel Benjamin
* 
*/
@RestController
@Tag(name = "02. Campaigns", description = "Endpoints for campaigns")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class CampaignController {
	private final AuthenticationHelper authHelper;
	@Value("${application.pilotage.integration-override}")
	private final String integrationOverride;
	/**
	* The campaign repository used to access 'campaign' table in DB 
	*/
	private final CampaignService campaignService;
	private final QuestionnaireModelService questionnaireService;
	private final PilotageService pilotageService;

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

	@Operation(summary = "Get campaign list for the current user")
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
		campaigns = pilotageService.getInterviewerCampaigns(authToken);
		// add questionnaires ids
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
	@ResponseStatus(HttpStatus.CREATED)
	public void createCampaign(@Valid @RequestBody CampaignInputDto campaignInputDto,
							   Authentication auth) {
		String userId = authHelper.getUserId(auth);
		log.info("User {} requests campaign {} creation", userId, campaignInputDto.id());
		campaignService.createCampaign(CampaignInputDto.toModel(campaignInputDto));
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
	@ResponseStatus(HttpStatus.OK)
	public void deleteCampaignById(@RequestParam("force") boolean force,
								   @IdValid @PathVariable(value = "id") String campaignId,
								   Authentication auth) {
		String userId = authHelper.getUserId(auth);
		log.info("Admin {} requests deletion of campaign {}", userId, campaignId);

		if(force ||
				(integrationOverride != null && integrationOverride.equals("true"))) {
			campaignService.delete(campaignId);
			log.info("Campaign with id {} deleted", campaignId);
			return;
		}

		String authToken = authHelper.getAuthToken(auth);
		if(pilotageService.isClosed(campaignId, authToken)) {
			campaignService.delete(campaignId);
			log.info("Campaign with id {} deleted", campaignId);
			return;
		}

		throw new CampaignDeletionException(String.format("Unable to delete campaign %s, campaign isn't closed", campaignId));
	}
}
