package fr.insee.queen.api.controller.surveyunit;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.service.surveyunit.PersonalizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* PersonalizationController is the Controller using to manage personalization
* 
* @author Claudel Benjamin
* 
*/
@RestController
@Tag(name = "06. Survey units")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
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
	@Operation(summary = "Get personalization for a survey unit")
	@GetMapping(path = "/survey-unit/{id}/personalization")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public String getPersonalizationBySurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId,
												 Authentication auth){
		log.info("GET personalization for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER);
		return personalizationService.getPersonalization(surveyUnitId);
	}
	
	/**
	* This method is using to update the personalization associated to a specific reporting unit 
	* 
	* @param personalizationValues the value to update
	* @param surveyUnitId	the id of reporting unit
	*
	*/
	@Operation(summary = "Update personalization for a survey unit")
	@PutMapping(path = "/survey-unit/{id}/personalization")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public void setPersonalization(@IdValid @PathVariable(value = "id") String surveyUnitId,
								   @NotNull @RequestBody ArrayNode personalizationValues,
								   Authentication auth) {
		log.info("PUT personalization for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER);
		personalizationService.updatePersonalization(surveyUnitId, personalizationValues);
	}
}