package fr.insee.queen.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.queen.api.domain.ParadataEvent;
import fr.insee.queen.api.repository.ParadataEventRepository;
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
	ParadataEventRepository paradataEventRepository;
	/**
	 * This method is using to update a specific survey unit
	 * 
	 * @param request
	 * @param surveyUnitUpdated
	 * @param id
	 * @return {@link HttpStatus}
	 */
	@ApiOperation(value = "Add a ParadataEnvent")
	@PostMapping(path = "/paradata")
	public ResponseEntity<Object> updateSurveyUnit(HttpServletRequest request,
			@RequestBody JSONObject paradataValue) {
		if (paradataValue == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {
			ParadataEvent paradataEvent = new ParadataEvent();
			paradataEvent.setValue(paradataValue);
			paradataEventRepository.save(paradataEvent);
			LOGGER.info("POST ParadataEvent resulting in {}", HttpStatus.OK);
			return new ResponseEntity<>(HttpStatus.OK);
		}
	}
}
