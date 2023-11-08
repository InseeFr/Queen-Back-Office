package fr.insee.queen.api.controller.surveyunit;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.controller.utils.AuthenticationHelper;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.dto.depositproof.PdfDepositProof;
import fr.insee.queen.api.dto.input.SurveyUnitCreateInputDto;
import fr.insee.queen.api.dto.input.SurveyUnitUpdateInputDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto;
import fr.insee.queen.api.service.exception.DepositProofException;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.service.pilotage.PilotageRole;
import fr.insee.queen.api.service.pilotage.PilotageService;
import fr.insee.queen.api.service.surveyunit.SurveyUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

/**
* SurveyUnitController is the Controller using to manage survey units
* 
* @author Claudel Benjamin
* 
*/
@RestController
@Tag(name = "06. Survey units", description = "Endpoints for survey units")
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
	private final PilotageService pilotageService;
	private final HabilitationComponent habilitationComponent;
	private final AuthenticationHelper authHelper;

	/**
	 * This method is used to get all surveyUnits Ids
	 */
	@Operation(summary = "Get all survey-units ids")
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
		habilitationComponent.checkHabilitations(auth, surveyUnitId, PilotageRole.INTERVIEWER, PilotageRole.REVIEWER);
		return surveyUnitService.getSurveyUnit(surveyUnitId);
	}
	
	/**
	* This method is used to update a survey-unit by id
	*/
	@Operation(summary = "Update survey-unit")
	@PutMapping(path = {"/survey-unit/{id}"})
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES + "||" + AuthorityRole.HAS_ROLE_INTERVIEWER)
	public void updateSurveyUnitById(@IdValid @PathVariable(value = "id") String surveyUnitId,
									 @Valid @RequestBody SurveyUnitUpdateInputDto surveyUnitInputDto,
									 Authentication auth) {
		log.info("PUT survey-unit for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, PilotageRole.INTERVIEWER);
		surveyUnitService.updateSurveyUnit(surveyUnitId, surveyUnitInputDto);
	}
	
	/**
	* This method is using to get all survey units associated to a specific campaign 
	* 
	* @param campaignId the id of campaign
	* @return List of {@link SurveyUnitSummaryDto}
	*/
	@Operation(summary = "Get list of survey units for a specific campaign")
	@GetMapping(path = "/campaign/{id}/survey-units")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public List<SurveyUnitSummaryDto> getListSurveyUnitByCampaign(@IdValid @PathVariable(value = "id") String campaignId,
																  Authentication auth) {
		log.info("GET survey-units for campaign with id {}", campaignId);

		List<SurveyUnitSummaryDto> surveyUnits;
		if(integrationOverride != null && integrationOverride.equals("true")) {
			surveyUnits = surveyUnitService.findByCampaignId(campaignId);
		} else {
			String authToken = authHelper.getAuthToken(auth);
			surveyUnits = pilotageService.getSurveyUnitsByCampaign(campaignId, authToken);
		}

		if(surveyUnits.isEmpty()) {
			throw new EntityNotFoundException(String.format("No survey units for the campaign with id %s", campaignId));
		}

		return surveyUnits;
	}
	
	@Operation(summary = "Get deposit proof for a survey unit")
	@GetMapping(value = "/survey-unit/{id}/deposit-proof")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public void generateDepositProof(@IdValid @PathVariable(value = "id") String surveyUnitId,
									 Authentication auth,
									 HttpServletResponse response) {
		log.info("GET deposit-proof with survey unit id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, PilotageRole.INTERVIEWER, PilotageRole.REVIEWER);

		String username = authHelper.getUserId(auth);
		PdfDepositProof depositProof = surveyUnitService.generateDepositProof(username, surveyUnitId);

		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "attachment; filename=\""+depositProof.filename()+"\"");

		try(OutputStream out = response.getOutputStream()){
			File pdfFile = depositProof.depositProof();
			out.write(Files.readAllBytes(pdfFile.toPath()));
			Files.delete(pdfFile.toPath());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DepositProofException();
		}
	}
	
	/**
	* This method is using to create a survey-unit
	* 
	* @param campaignId the id of campaign
	*/
	@Operation(summary = "Create/Update a survey unit")
	@PostMapping(path = "/campaign/{id}/survey-unit")
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	public ResponseEntity<Void> createUpdateSurveyUnit(@IdValid @PathVariable(value = "id") String campaignId,
													  @Valid @RequestBody SurveyUnitCreateInputDto surveyUnitCreateInputDto,
													  Authentication auth){
		log.info("POST survey-unit with id {}", surveyUnitCreateInputDto.id());
		if(surveyUnitService.existsById(surveyUnitCreateInputDto.id())) {
			updateSurveyUnitById(surveyUnitCreateInputDto.id(), SurveyUnitCreateInputDto.toUpdateDto(surveyUnitCreateInputDto), auth);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		log.info("Create survey-unit with id {}", surveyUnitCreateInputDto.id());
		surveyUnitService.createSurveyUnit(campaignId, surveyUnitCreateInputDto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	* This method is using to delete a survey-unit
	* 
	* @param surveyUnitId the id of survey-unit
	*/
	@Operation(summary = "Delete a survey unit")
	@DeleteMapping(path = "/survey-unit/{id}")
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId){
		log.info("DELETE survey-unit with id {}", surveyUnitId);
		surveyUnitService.delete(surveyUnitId);
	}
}
