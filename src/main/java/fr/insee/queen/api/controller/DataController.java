package fr.insee.queen.api.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.data.DataDto;
import fr.insee.queen.api.service.DataService;
import fr.insee.queen.api.service.SurveyUnitService;
import fr.insee.queen.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

/**
* DataController is the Controller using to manage {@link Data} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class DataController {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);
	
	/**
	* The data repository using to access to table 'data' in DB 
	*/
	@Autowired
	private DataService dataService;
	
	/**
	* The reporting unit repository using to access to table 'reporting_unit' in DB 
	*/
	@Autowired
	private SurveyUnitService surveyUnitService;
	
	@Autowired
	private UtilsService utilsService;
	
	/**
	* This method is using to get the data associated to a specific reporting unit 
	* 
	* @param id the id of reporting unit
	* @return {@link DataDto} the data associated to the reporting unit
	 * @throws Exception 
	*/
	@ApiOperation(value = "Get data by reporting unit Id ")
	@GetMapping(path = "/survey-unit/{id}/data")
	public ResponseEntity<Object>  getDataBySurveyUnit(@PathVariable(value = "id") String id, HttpServletRequest request) {
			Optional<SurveyUnit> surveyUnitOptional = surveyUnitService.findById(id);
			if (!surveyUnitOptional.isPresent()) {
				LOGGER.error("GET data for reporting unit with id {} resulting in 404", id);
				return ResponseEntity.notFound().build();
			}
			String userId = utilsService.getUserId(request);
			if(!userId.equals("GUEST") && !utilsService.checkHabilitation(request, id, Constants.INTERVIEWER)) {
				LOGGER.error("GET data for reporting unit with id {} resulting in 403", id);
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
			LOGGER.info("GET comment for reporting unit with id {} resulting in 200", id);
			Optional<Data> dataOptional = dataService.findBySurveyUnitId(id);
			if (!dataOptional.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(dataOptional.get().getValue(), HttpStatus.OK);
	}

	
	/**
	* This method is using to update the data associated to a specific reporting unit 
	* 
	* @param dataValue	the value to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if data is not found, else {@link HttpStatus 200}
	* 
	*/
	@ApiOperation(value = "Update data by reporting unit Id ")
	@PutMapping(path = "/survey-unit/{id}/data")
	public ResponseEntity<Object> setData(@RequestBody JsonNode dataValue, @PathVariable(value = "id") String id, HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if(!userId.equals("GUEST") && !utilsService.checkHabilitation(request, id, Constants.INTERVIEWER)) {
			LOGGER.error("PUT data for reporting unit with id {} resulting in 403", id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		dataService.updateDataImproved(id, dataValue);
		LOGGER.info("PUT data for reporting unit with id {} resulting in 200", id);
		return ResponseEntity.ok().build();
	}
}
