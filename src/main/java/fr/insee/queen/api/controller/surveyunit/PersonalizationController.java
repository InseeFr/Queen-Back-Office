package fr.insee.queen.api.controller.surveyunit;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.service.pilotage.PilotageRole;
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
* Handle personalization data for a survey unit
*/
@RestController
@Tag(name = "06. Survey units")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class PersonalizationController {
	private final PersonalizationService personalizationService;
	private final HabilitationComponent habilitationComponent;
	
	/**
	* Retrieve the personalization data of a survey unit
	* 
	* @param surveyUnitId the id of the survey unit
	* @param auth authentication object
	* @return {@link String} the personalization associated to the survey unit
	*/
	@Operation(summary = "Get personalization for a survey unit")
	@GetMapping(path = "/survey-unit/{id}/personalization")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public String getPersonalizationBySurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId,
												 Authentication auth){
		log.info("GET personalization for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, PilotageRole.INTERVIEWER);
		return personalizationService.getPersonalization(surveyUnitId);
	}
	
	/**
	* Update the personalization data associated to the survey unit
	* 
	* @param personalizationValues the value to update
	* @param surveyUnitId the id of the survey unit
	* @param auth authentication object
	*/
	@Operation(summary = "Update personalization for a survey unit")
	@PutMapping(path = "/survey-unit/{id}/personalization")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public void setPersonalization(@IdValid @PathVariable(value = "id") String surveyUnitId,
								   @NotNull @RequestBody ArrayNode personalizationValues,
								   Authentication auth) {
		log.info("PUT personalization for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, PilotageRole.INTERVIEWER);
		personalizationService.updatePersonalization(surveyUnitId, personalizationValues);
	}
}