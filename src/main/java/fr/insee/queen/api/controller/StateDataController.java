package fr.insee.queen.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.data.DataDto;
import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitOkNokDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitResponseDto;
import fr.insee.queen.api.service.StateDataService;
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
public class StateDataController {
	private static final Logger LOGGER = LoggerFactory.getLogger(StateDataController.class);
	
	/**
	* The data repository using to access to table 'data' in DB 
	*/
	@Autowired
	private StateDataService stateDataService;
	
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
	*/
	@ApiOperation(value = "Get state-data by survey-unit Id ")
	@GetMapping(path = "/survey-unit/{id}/state-data")
	public ResponseEntity<StateDataDto>  getStateDataBySurveyUnit(@PathVariable(value = "id") String id, HttpServletRequest request){
		Optional<SurveyUnit> surveyUnitOptional = surveyUnitService.findById(id);
		if (!surveyUnitOptional.isPresent() || surveyUnitOptional.get().getStateData() == null) {
			LOGGER.error("GET state-data for reporting unit with id {} resulting in 404", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		String userId = utilsService.getUserId(request);
		if(!userId.equals("GUEST") && !utilsService.checkHabilitation(request, id, Constants.INTERVIEWER)) {
			LOGGER.error("GET state-data for reporting unit with id {} resulting in 403", id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		LOGGER.info("GET state-data for reporting unit with id {} resulting in 200", id);
		return new ResponseEntity<>(new StateDataDto(surveyUnitOptional.get().getStateData()), HttpStatus.OK);		
	}
	
	@ApiOperation(value = "Get state-data for all survey-units defined in request body ")
	@PostMapping(path = "survey-units/state-data")
	public ResponseEntity<SurveyUnitOkNokDto>  getStateDataBySurveyUnits(@RequestBody List<String> lstSurveyUnitId, HttpServletRequest request){
		List<SurveyUnit> lstSurveyUnit = (List<SurveyUnit>) surveyUnitService.findByIds(lstSurveyUnitId);		
		List<String> surveyUnitsIds = lstSurveyUnit.stream().map(SurveyUnit::getId).collect(Collectors.toList());
		List<SurveyUnitResponseDto> surveyUnitsNOK = lstSurveyUnitId.stream()
				.filter(su -> !surveyUnitsIds.contains(su))
				.map(su -> new SurveyUnitResponseDto(su))
				.collect(Collectors.toList());
		List<SurveyUnitResponseDto> surveyUnitsOK = lstSurveyUnit.stream()
				.map(su -> new SurveyUnitResponseDto(su.getId(), null, null, null, null, new StateDataDto(su.getStateData())))
				.collect(Collectors.toList());
		return new ResponseEntity<>(new SurveyUnitOkNokDto(surveyUnitsOK, surveyUnitsNOK), HttpStatus.OK);		
	}
	
	/**
	* This method is using to update the state-data associated to a specific reporting unit
	* 
	* @param dataValue	the value to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if data is not found, else {@link HttpStatus 200}
	 * @throws Exception 
	* 
	*/
	@ApiOperation(value = "Update data by reporting unit Id ")
	@PutMapping(path = "/survey-unit/{id}/state-data")
	public ResponseEntity<Object> setStateData(@RequestBody JsonNode dataValue, HttpServletRequest request, @PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		if(!userId.equals("GUEST") && !utilsService.checkHabilitation(request, id, Constants.INTERVIEWER)) {
			LOGGER.error("PUT state-data for reporting unit with id {} resulting in 403", id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return stateDataService.updateStateData(id, dataValue);
		
	}
}
