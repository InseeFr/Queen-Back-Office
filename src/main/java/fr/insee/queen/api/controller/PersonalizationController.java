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
import fr.insee.queen.api.domain.Personalization;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.service.PersonalizationService;
import fr.insee.queen.api.service.SurveyUnitService;
import fr.insee.queen.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

/**
* PersonalizationController is the Controller using to manage {@link Personalization} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class PersonalizationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonalizationController.class);
	
	/**
	* The personalization repository using to access to table 'personalization' in DB 
	*/
	@Autowired
	private PersonalizationService personalizationService;
	
	/**
	* The reporting unit repository using to access to table 'reporting_unit' in DB 
	*/
	@Autowired
	private SurveyUnitService surveyUnitService;
	
	@Autowired
	private UtilsService utilsService;
	
	/**
	* This method is using to get the personalization associated to a specific reporting unit 
	* 
	* @param id the id of reporting unit
	* @return {@link PersonalizationDto} the personalization associated to the reporting unit
	*/
	@ApiOperation(value = "Get personalization by reporting unit Id ")
	@GetMapping(path = "/survey-unit/{id}/personalization")
	public ResponseEntity<Object>  getPersonalizationBySurveyUnit(@PathVariable(value = "id") String id, HttpServletRequest request){
		Optional<SurveyUnit> surveyUnitOptional = surveyUnitService.findById(id);
		if (!surveyUnitOptional.isPresent()) {
			LOGGER.error("GET personalization for reporting unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		}
		String userId = utilsService.getUserId(request);
		if(!userId.equals("GUEST") && !utilsService.checkHabilitation(request, id, Constants.INTERVIEWER)) {
			LOGGER.error("GET personalization for reporting unit with id {} resulting in 403", id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		LOGGER.info("GET personalization for reporting unit with id {} resulting in 200", id);
		Optional<Personalization> personalizationOptional = personalizationService.findBySurveyUnitId(id);
		if (!personalizationOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(personalizationOptional.get().getValue(), HttpStatus.OK);
	}
	
	/**
	* This method is using to update the personalization associated to a specific reporting unit 
	* 
	* @param personalizationValue	the value to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if personalization is not found, else {@link HttpStatus 200}
	* 
	*/
	@ApiOperation(value = "Update personalization by reporting unit Id ")
	@PutMapping(path = "/survey-unit/{id}/personalization")
	public ResponseEntity<Object> setPersonalization(@RequestBody JsonNode personalizationValues, @PathVariable(value = "id") String id, HttpServletRequest request) {
		Optional<SurveyUnit> surveyUnitOptional = surveyUnitService.findById(id);
		if (!surveyUnitOptional.isPresent()) {
			LOGGER.info("PUT personalization for reporting unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		}
		String userId = utilsService.getUserId(request);
		if(!userId.equals("GUEST") && !utilsService.checkHabilitation(request, id, Constants.INTERVIEWER)) {
			LOGGER.info("PUT personalization for reporting unit with id {} resulting in 403", id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		personalizationService.updatePersonalization(surveyUnitOptional.get(), personalizationValues);
		LOGGER.info("PUT personalization for reporting unit with id {} resulting in 200", id);
		return ResponseEntity.ok().build();
		
	}
	
	
}