package fr.insee.queen.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.domain.ParadataEvent;
import fr.insee.queen.api.service.ParadataEventService;
import fr.insee.queen.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

/**
 * ParadataEnventController is the Controller using to manage {@link ParadataEvent}
 * entity
 * 
 * @author Corcaud Samuel
 * 
 */
@RestController
@RequestMapping(path = "/api")
public class ParadataEventController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ParadataEventController.class);
	
	@Autowired
	private UtilsService utilsService;
	
	@Autowired
	ParadataEventService paradataEventService;
	/**
	 * This method is used to save a pardata event
	 * 
	 * @param request
	 * @param paradataValue
	 * @return {@link HttpStatus}
	 */
	@ApiOperation(value = "Add a ParadataEvent")
	@PostMapping(path = "/paradata")
	public ResponseEntity<Object> updateSurveyUnit(HttpServletRequest request,
			@RequestBody JsonNode paradataValue) {
		if (paradataValue == null) {
			LOGGER.info("POST ParadataEvent resulting in {}, in request body is missing", HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {
			ParadataEvent paradataEvent = new ParadataEvent();
			paradataEvent.setValue(paradataValue);
			paradataEventService.save(paradataEvent);
			LOGGER.info("POST ParadataEvent resulting in {}", HttpStatus.OK);
			return new ResponseEntity<>(HttpStatus.OK);
		}
	}
}
