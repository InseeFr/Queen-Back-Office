package fr.insee.queen.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.service.PersonalizationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
* PersonalizationController is the Controller using to manage personalization
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
public class PersonalizationController {
	
	/**
	* The personalization repository using to access to table 'personalization' in DB 
	*/
	private final PersonalizationService personalizationService;
	/**
	* The reporting unit repository using to access to table 'reporting_unit' in DB 
	*/
	private final HabilitationComponent habilitationComponent;
	
	/**
	* This method is using to get the personalization associated to a specific reporting unit 
	* 
	* @param surveyUnitId the id of reporting unit
	* @return {@link String} the personalization associated to the reporting unit
	*/
	@Operation(summary = "Get personalization by reporting unit Id ")
	@GetMapping(path = "/survey-unit/{id}/personalization")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public String  getPersonalizationBySurveyUnit(@PathVariable(value = "id") String surveyUnitId, Authentication auth){
		log.info("GET personalization for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER);
		return personalizationService.getPersonalization(surveyUnitId).value();
	}
	
	/**
	* This method is using to update the personalization associated to a specific reporting unit 
	* 
	* @param personalizationValues the value to update
	* @param surveyUnitId	the id of reporting unit
	* @return {@link HttpStatus 404} if personalization is not found, else {@link HttpStatus 200}
	* 
	*/
	@Operation(summary = "Update personalization by reporting unit Id ")
	@PutMapping(path = "/survey-unit/{id}/personalization")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public HttpStatus setPersonalization(@PathVariable(value = "id") String surveyUnitId,
										 @RequestBody JsonNode personalizationValues,
										 Authentication auth) {
		log.info("PUT personalization for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER);
		personalizationService.updatePersonalization(surveyUnitId, personalizationValues);
		return HttpStatus.OK;
		
	}
	
	
}