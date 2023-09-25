package fr.insee.queen.api.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.controller.utils.AuthenticationHelper;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.input.SurveyUnitInputDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitDepositProofDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitTempZoneDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.service.CampaignService;
import fr.insee.queen.api.service.PilotageApiService;
import fr.insee.queen.api.service.SurveyUnitService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* SurveyUnitController is the Controller using to manage {@link SurveyUnit} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class SurveyUnitController {
	@Value("${application.pilotage.integration-override}")
	private final String integrationOverride;
	/**
	* The survey unit repository using to access to table 'survey_unit' in DB 
	*/
	private final SurveyUnitService surveyUnitService;
	private final PilotageApiService pilotageApiService;
	private final HabilitationComponent habilitationComponent;
	private final AuthenticationHelper authHelper;
	private final CampaignService campaignService;

	/**
	 * This method is used to get all surveyUnits Ids
	 */
	@Operation(summary = "Get all survey-units Ids")
	@GetMapping(path = "/survey-units")
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	public List<String> getSurveyUnitIds(){
		log.info("GET survey-units");
		return surveyUnitService.findAllSurveyUnitIds();
	}

	/**
	* This method is used to get a survey-unit by id
	*/
	@Operation(summary = "Get survey-unit")
	@GetMapping(path = "/survey-unit/{id}")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public SurveyUnitDto getSurveyUnitById(@IdValid @PathVariable(value = "id") String surveyUnitId,
										   Authentication auth) {
		log.info("GET survey-units with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER, Constants.REVIEWER);
		return surveyUnitService.getSurveyUnit(surveyUnitId);
	}
	
	/**
	* This method is used to update a survey-unit by id
	*/
	@Operation(summary = "Put survey-unit")
	@PutMapping(path = {"/survey-unit/{id}"})
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES + "||" + AuthorityRole.INTERVIEWER)
	public void updateSurveyUnitById(@IdValid @PathVariable(value = "id") String surveyUnitId,
									 @RequestBody SurveyUnitInputDto surveyUnitInputDto,
									 Authentication auth) {
		log.info("PUT survey-unit for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER);
		surveyUnitService.updateSurveyUnit(surveyUnitId, surveyUnitInputDto);
	}


	/**
	 * This method is used to post a survey-unit by id to a temp-zone
	 */
	@Operation(summary = "Post survey-unit to temp-zone")
	@PostMapping(path = "/survey-unit/{id}/temp-zone")
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES + "||" + AuthorityRole.INTERVIEWER)
	@ResponseStatus(HttpStatus.CREATED)
	public void postSurveyUnitByIdInTempZone(@IdValid @PathVariable(value = "id") String surveyUnitId,
											 @NotNull @RequestBody ObjectNode surveyUnit,
											 Authentication auth) {
		log.info("POST survey-unit to temp-zone");
		String userId = authHelper.getUserId(auth);
		surveyUnitService.saveSurveyUnitToTempZone(surveyUnitId, userId, surveyUnit);
	}

	/**
	 * This method is used to retrieve survey-units in temp-zone
	 */
	@Operation(summary = "GET all survey-units in temp-zone")
	@GetMapping(path = "/survey-units/temp-zone")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public List<SurveyUnitTempZoneDto> getSurveyUnitsInTempZone() {
		log.info("GET all survey-units in temp-zone");
		return surveyUnitService.getAllSurveyUnitTempZoneDto();
	}
	
	/**
	* This method is using to get all survey units associated to a specific campaign 
	* 
	* @param campaignId the id of campaign
	* @return List of {@link SurveyUnitDto}
	*/
	
	
	@Operation(summary = "Get list of survey units by campaign Id ")
	@GetMapping(path = "/campaign/{id}/survey-units")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public List<SurveyUnitSummaryDto> getListSurveyUnitByCampaign(@IdValid @PathVariable(value = "id") String campaignId,
																  Authentication auth) {
		log.info("GET survey-units for campaign with id {}", campaignId);
		if(!campaignService.existsById(campaignId)) {
			throw new EntityNotFoundException(String.format("Campaign %s was not found", campaignId));
		}

		List<SurveyUnitSummaryDto> surveyUnits;
		String userId = authHelper.getUserId(auth);
		if(!userId.equals(Constants.GUEST) &&
		  !(integrationOverride != null && integrationOverride.equals("true"))) {
			String authToken = authHelper.getAuthToken(auth);
			surveyUnits = pilotageApiService.getSurveyUnitsByCampaign(campaignId, authToken);
		} else {
			surveyUnits = surveyUnitService.findByCampaignId(campaignId);
		}

		if(surveyUnits.isEmpty()) {
			throw new EntityNotFoundException(String.format("No survey units for the campaign with id %s", campaignId));
		}

		return surveyUnits;
	}
	
	@Operation(summary = "Get deposit proof for a SU ")
	@GetMapping(value = "/survey-unit/{id}/deposit-proof")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public void generateDepositProof(@IdValid @PathVariable(value = "id") String surveyUnitId,
									 Authentication auth,
									 HttpServletResponse response) {
		log.info("GET deposit-proof with survey unit id {}", surveyUnitId);
		SurveyUnitDepositProofDto surveyUnit = surveyUnitService.getSurveyUnitDepositProof(surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER, Constants.REVIEWER);

		if (surveyUnit.stateData() == null) {
			throw new EntityNotFoundException(String.format("State data for survey unit %s was not found", surveyUnitId));
	    }
		String username = authHelper.getUserId(auth);
		surveyUnitService.generateDepositProof(username, surveyUnit, response);
	}
	
	/**
	* This method is using to create a survey-unit
	* 
	* @param campaignId the id of campaign
	*/
	@Operation(summary = "Post survey-unit")
	@PostMapping(path = "/campaign/{id}/survey-unit")
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	public ResponseEntity<Void> createSurveyUnit(@IdValid @PathVariable(value = "id") String campaignId,
													  @Valid @RequestBody SurveyUnitInputDto surveyUnitInputDto,
													  Authentication auth){
		log.info("POST survey-unit with id {}", surveyUnitInputDto.id());
		if(surveyUnitService.existsById(surveyUnitInputDto.id())) {
			updateSurveyUnitById(surveyUnitInputDto.id(), surveyUnitInputDto, auth);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		log.info("Create survey-unit with id {}", surveyUnitInputDto.id());
		surveyUnitService.createSurveyUnit(campaignId, surveyUnitInputDto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	* This method is using to delete a survey-unit
	* 
	* @param surveyUnitId the id of survey-unit
	*/
	@Operation(summary = "Delete survey-unit")
	@DeleteMapping(path = "/survey-unit/{id}")
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId){
		log.info("DELETE survey-unit with id {}", surveyUnitId);
		surveyUnitService.delete(surveyUnitId);
	}
}
