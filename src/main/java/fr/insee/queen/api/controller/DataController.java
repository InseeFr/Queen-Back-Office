package fr.insee.queen.api.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.dto.data.DataDto;
import fr.insee.queen.api.service.DataService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* DataController is the Controller using to manage survey unit data
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class DataController {
	/**
	* The data repository using to access to table 'data' in DB 
	*/
	private final DataService dataService;
	/**
	* The reporting unit repository using to access to table 'reporting_unit' in DB 
	*/
	private final HabilitationComponent habilitationComponent;
	
	/**
	* This method is using to get the data associated to a specific reporting unit 
	* 
	* @param surveyUnitId the id of reporting unit
	* @return {@link DataDto} the data associated to the reporting unit
	*/
	@Operation(summary = "Get data by reporting unit Id ")
	@GetMapping(path = "/survey-unit/{id}/data")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public String getDataBySurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId,
									  Authentication auth) {
		log.info("GET Data for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER);
		return dataService.getData(surveyUnitId);
	}

	
	/**
	* This method is using to update the data associated to a specific reporting unit 
	* 
	* @param dataValue	the value to update
	* @param surveyUnitId	the id of reporting unit
	*
	*/
	@Operation(summary = "Update data by reporting unit Id ")
	@PutMapping(path = "/survey-unit/{id}/data")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public void updateData(@NotNull @RequestBody ObjectNode dataValue,
						@IdValid @PathVariable(value = "id") String surveyUnitId,
						Authentication auth) {
		log.info("PUT data for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER);
		dataService.updateData(surveyUnitId, dataValue);
	}
}
