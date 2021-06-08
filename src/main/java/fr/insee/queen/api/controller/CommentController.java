package fr.insee.queen.api.controller;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.domain.Comment;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.comment.CommentDto;
import fr.insee.queen.api.service.CommentService;
import fr.insee.queen.api.service.SurveyUnitService;
import fr.insee.queen.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

/**
* CommentController is the Controller using to manage {@link Comment} entity
* 
* @author Claudel Benjamin
* 
*/
@RestController
@RequestMapping(path = "/api")
public class CommentController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);
	
	/**
	* The comment repository using to access to table 'comment' in DB 
	*/
	@Autowired
	private CommentService commentService;
	
	/**
	* The reporting unit repository using to access to table 'reporting_unit' in DB 
	*/
	@Autowired
	private SurveyUnitService surveyUnitservice;
	
	@Autowired
	private UtilsService utilsService;
	
	/**
	* This method is using to get the comment associated to a specific reporting unit 
	* 
	* @param id the id of reporting unit
	* @return {@link CommentDto} the comment associated to the reporting unit
	*/
	@ApiOperation(value = "Get comment for reporting unit Id ")
	@GetMapping(path = "/survey-unit/{id}/comment")
	public ResponseEntity<Object> getCommentBySurveyUnit(@PathVariable(value = "id") String id, HttpServletRequest request){
		Optional<SurveyUnit> surveyUnitOptional = surveyUnitservice.findById(id);
		if (!surveyUnitOptional.isPresent()) {
			LOGGER.error("GET comment for reporting unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
	    }
	    String userId = utilsService.getUserId(request);
		if(!userId.equals("GUEST") && !utilsService.checkHabilitation(request, id, Constants.INTERVIEWER)) {
			LOGGER.error("GET comment for reporting unit with id {} resulting in 403", id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		LOGGER.info("GET comment for reporting unit with id {} resulting in 200", id);
		Optional<Comment> commentOptional = commentService.findBySurveyUnitId(id);
		if (!commentOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(commentOptional.get().getValue(), HttpStatus.OK);
	}
	
	/**
	* This method is using to update the comment associated to a specific reporting unit 
	* 
	* @param commentValue the value to update
	* @param id	the id of reporting unit
	* @return {@link HttpStatus 404} if comment is not found, else {@link HttpStatus 200}
	* @throws ParseException 
	* @throws SQLException 
	* 
	*/
	@ApiOperation(value = "Update the comment by reporting unit Id ")
	@PutMapping(path = "/survey-unit/{id}/comment")
	public ResponseEntity<Object> setComment(@RequestBody JsonNode commentValue, @PathVariable(value = "id") String id, HttpServletRequest request) {
		Optional<SurveyUnit> surveyUnitOptional = surveyUnitservice.findById(id);
		if (!surveyUnitOptional.isPresent()) {
			LOGGER.error("PUT comment for reporting unit with id {} resulting in 404", id);
			return ResponseEntity.notFound().build();
		}
		String userId = utilsService.getUserId(request);
		if(!userId.equals("GUEST") && !utilsService.checkHabilitation(request, id, Constants.INTERVIEWER)) {
			LOGGER.error("PUT comment for reporting unit with id {} resulting in 403", id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		
		commentService.updateComment(surveyUnitOptional.get(), commentValue);
		LOGGER.info("PUT comment for reporting unit with id {} resulting in 200", id);
		return ResponseEntity.ok().build();
		
	}	
}
