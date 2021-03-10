package fr.insee.queen.api.controller;

import java.sql.SQLException;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
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

import fr.insee.queen.api.domain.Data;
import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.domain.StateStateData;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.data.DataDto;
import fr.insee.queen.api.dto.stateData.StateDataDto;
import fr.insee.queen.api.repository.DataRepository;
import fr.insee.queen.api.repository.StateDataRepository;
import fr.insee.queen.api.repository.SurveyUnitRepository;
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
	private StateDataRepository stateDataRepository;
	
	/**
	* The reporting unit repository using to access to table 'reporting_unit' in DB 
	*/
	@Autowired
	private SurveyUnitRepository surveyUnitRepository;
	
	/**
	* This method is using to get the data associated to a specific reporting unit 
	* 
	* @param id the id of reporting unit
	* @return {@link DataDto} the data associated to the reporting unit
	*/
	@ApiOperation(value = "Get data by reporting unit Id ")
	@GetMapping(path = "/survey-unit/{id}/state-data")
	public ResponseEntity<Object>  getDataBySurveyUnit(@PathVariable(value = "id") String id){
		Optional<SurveyUnit> surveyUnitOptional = surveyUnitRepository.findById(id);
		if (!surveyUnitOptional.isPresent()) {
			LOGGER.info("GET comment for reporting unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			LOGGER.info("GET comment for reporting unit with id {} resulting in 200", id);
			if (surveyUnitOptional.get().getStateData() == null) {
				return new ResponseEntity<>(new JSONObject(), HttpStatus.OK);
			}else {
				return new ResponseEntity<>(new StateDataDto(surveyUnitOptional.get().getStateData()), HttpStatus.OK);
			}
		}
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
	@PutMapping(path = "/survey-unit/{id}/state-data")
	public ResponseEntity<Object> setData(@RequestBody JSONObject dataValue, @PathVariable(value = "id") String id) throws ParseException, SQLException {
		Optional<SurveyUnit> surveyUnitOptional = surveyUnitRepository.findById(id);
		if (!surveyUnitOptional.isPresent()) {
			LOGGER.info("PUT data for reporting unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		} else {
			Optional<StateData> stateDataOptional = stateDataRepository.findBySurveyUnit_id(id);
			StateData stateData;
			if (!stateDataOptional.isPresent()) {
				stateData = new StateData();
				stateData.setSurveyUnit(surveyUnitOptional.get());
			}else {
				stateData = stateDataOptional.get();
			}
			
			try {
				Long date = (Long) dataValue.get("date");
				String state = (String) dataValue.get("state");
				Integer currentPage = (Integer) dataValue.get("currentPage");
				if(date != null) {
					stateData.setDate(date);
				}
				if(state != null) {
					stateData.setState(StateStateData.valueOf(state));
				}
				if(currentPage != null) {
					stateData.setCurrentPage(currentPage);
				}
				stateDataRepository.save(stateData);
			}
			catch(Exception e) {
				LOGGER.info("PUT state-data resulting in 400");
				return ResponseEntity.badRequest().build();
			}
			
			LOGGER.info("PUT data for reporting unit with id {} resulting in 200", id);
			return ResponseEntity.ok().build();
		}
	}
}
