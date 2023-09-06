package fr.insee.queen.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.dto.data.DataDto;
import fr.insee.queen.api.service.DataService;
import fr.insee.queen.api.service.HabilitationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class DataController {
	/**
	* The data repository using to access to table 'data' in DB 
	*/
	private final DataService dataService;
	/**
	* The reporting unit repository using to access to table 'reporting_unit' in DB 
	*/
	private final HabilitationService habilitationService;
	
	/**
	* This method is using to get the data associated to a specific reporting unit 
	* 
	* @param surveyUnitId the id of reporting unit
	* @return {@link DataDto} the data associated to the reporting unit
	*/
	@Operation(summary = "Get data by reporting unit Id ")
	@GetMapping(path = "/survey-unit/{id}/data")
	public String getDataBySurveyUnit(@PathVariable(value = "id") String surveyUnitId, HttpServletRequest request) {
		log.info("GET Data for reporting unit with id {}", surveyUnitId);
		habilitationService.checkHabilitations(request, surveyUnitId, Constants.INTERVIEWER);
		return dataService.getData(surveyUnitId).value();
	}

	
	/**
	* This method is using to update the data associated to a specific reporting unit 
	* 
	* @param dataValue	the value to update
	* @param surveyUnitId	the id of reporting unit
	* @return {@link HttpStatus 404} if data is not found, else {@link HttpStatus 200}
	* 
	*/
	@Operation(summary = "Update data by reporting unit Id ")
	@PutMapping(path = "/survey-unit/{id}/data")
	public ResponseEntity<Object> setData(@RequestBody JsonNode dataValue, @PathVariable(value = "id") String surveyUnitId, HttpServletRequest request) {
		log.info("PUT data for reporting unit with id {}", surveyUnitId);
		habilitationService.checkHabilitations(request, surveyUnitId, Constants.INTERVIEWER);
		dataService.updateData(surveyUnitId, dataValue);
		return ResponseEntity.ok().build();
	}
}
